/* Copyright (C) 2019  olie.xdev <olie.xdev@googlemail.com>
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

 /*
 * Based on source-code by weliem/blessed-android
 */
package com.health.openscale.core.bluetooth;

import android.content.Context;
import android.util.Pair;

import com.health.openscale.core.datatypes.ScaleMeasurement;
import com.health.openscale.core.datatypes.ScaleUser;
import com.health.openscale.core.utils.Converters;
import com.welie.blessed.BluetoothBytesParser;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.UUID;
import java.util.Vector;

import timber.log.Timber;

import static com.welie.blessed.BluetoothBytesParser.FORMAT_UINT16;
import static com.welie.blessed.BluetoothBytesParser.FORMAT_UINT8;

class BluetoothGattUuidBF600 extends BluetoothGattUuid {
    public static final UUID SERVICE_BEURER_CUSTOM_BF600 = fromShortCode(0xfff0);
    public static final UUID CHARACTERISTIC_BEURER_BF600_SCALE_SETTING = fromShortCode(0xfff1);
    public static final UUID CHARACTERISTIC_BEURER_BF600_USER_LIST = fromShortCode(0xfff2);
    public static final UUID CHARACTERISTIC_BEURER_BF600_ACTIVITY_LEVEL = fromShortCode(0xfff3);
    public static final UUID CHARACTERISTIC_BEURER_BF600_TAKE_MEASUREMENT = fromShortCode(0xfff4);
    public static final UUID CHARACTERISTIC_BEURER_BF600_REFER_WEIGHT_BF = fromShortCode(0xfff5);
}

public class BluetoothBeurerBF600 extends BluetoothStandardWeightProfile {

    ScaleMeasurement scaleMeasurement;
    private Vector<ScaleUser> scaleUserList;
    static final int BF600_MAX_USERS = 8;
    String deviceName;

    public BluetoothBeurerBF600(Context context, String name) {
        super(context);
        scaleMeasurement = new ScaleMeasurement();
        scaleUserList = new Vector<ScaleUser>();
        deviceName = name;    }

    @Override
    public String driverName() {
        return "Beurer " + deviceName;
    }

    @Override
    protected void writeBirthday() {
        BluetoothBytesParser parser = new BluetoothBytesParser();
        parser.setDateTime(dateToCalender(this.selectedUser.getBirthday()));
        writeBytes(BluetoothGattUuid.SERVICE_USER_DATA, BluetoothGattUuid.CHARACTERISTIC_USER_DATE_OF_BIRTH,
                Arrays.copyOfRange(parser.getValue(), 0, 3));
    }

    @Override
    protected void writeActivityLevel() {
        Converters.ActivityLevel al = selectedUser.getActivityLevel();
        BluetoothBytesParser parser = new BluetoothBytesParser(new byte[]{0});
        parser.setIntValue(al.toInt() + 1, FORMAT_UINT8, 0);
        Timber.d(String.format("setCurrentUserData Activity level: %d", al.toInt() + 1));
        writeBytes(BluetoothGattUuidBF600.SERVICE_BEURER_CUSTOM_BF600,
                BluetoothGattUuidBF600.CHARACTERISTIC_BEURER_BF600_ACTIVITY_LEVEL, parser.getValue());
    }

    @Override
    protected void requestMeasurement() {
        BluetoothBytesParser parser = new BluetoothBytesParser(new byte[]{0});
        parser.setIntValue(0x00, FORMAT_UINT8, 0);
        Timber.d(String.format("requestMeasurement BEURER 0xFFF4 magic: 0x00"));
        writeBytes(BluetoothGattUuidBF600.SERVICE_BEURER_CUSTOM_BF600,
                BluetoothGattUuidBF600.CHARACTERISTIC_BEURER_BF600_TAKE_MEASUREMENT, parser.getValue());
    }

    @Override
    protected void setNotifyVendorSpecificUserList() {
        setNotificationOn(BluetoothGattUuidBF600.SERVICE_BEURER_CUSTOM_BF600,
                BluetoothGattUuidBF600.CHARACTERISTIC_BEURER_BF600_USER_LIST);
    }

    @Override
    protected synchronized void requestVendorSpecificUserList() {
        scaleUserList.clear();
        BluetoothBytesParser parser = new BluetoothBytesParser();
        parser.setIntValue(0x00, FORMAT_UINT8);
        writeBytes(BluetoothGattUuidBF600.SERVICE_BEURER_CUSTOM_BF600, BluetoothGattUuidBF600.CHARACTERISTIC_BEURER_BF600_USER_LIST,
                parser.getValue());
        stopMachineState();
    }

    @Override
    public void onBluetoothNotify(UUID characteristic, byte[] value) {
        if (characteristic.equals(BluetoothGattUuidBF600.CHARACTERISTIC_BEURER_BF600_USER_LIST)) {
            Timber.d(String.format("Got user data: <%s>", byteInHex(value)));
            BluetoothBytesParser parser = new BluetoothBytesParser(value);
            int userListStatus = parser.getIntValue(FORMAT_UINT8);
            if (userListStatus == 2) {
                Timber.d("scale have no users!");
                storeUserScaleConsentCode(selectedUser.getId(), -1);
                storeUserScaleIndex(selectedUser.getId(), -1);
                jumpNextToStepNr(SM_STEPS.REGISTER_NEW_SCALE_USER.ordinal());
                resumeMachineState();
                return;
            }
            else if (userListStatus == 1) {
                for (int i = 0; i < scaleUserList.size(); i++) {
                    if (i == 0) {
                        Timber.d("scale user list:");
                    }
                    Timber.d("\n" + (i + 1) + ". " + scaleUserList.get(i));
                }
                if ((scaleUserList.size() == 0)) {
                    storeUserScaleConsentCode(selectedUser.getId(), -1);
                    storeUserScaleIndex(selectedUser.getId(), -1);
                    jumpNextToStepNr(SM_STEPS.REGISTER_NEW_SCALE_USER.ordinal());
                    resumeMachineState();
                    return;
                }
                if (getUserScaleIndex(selectedUser.getId()) == -1 || getUserScaleConsent(selectedUser.getId()) == -1)  {
                    chooseExistingScaleUser(scaleUserList);
                    return;
                }
                resumeMachineState();
                return;
            }
            int index = parser.getIntValue(FORMAT_UINT8);
            String initials = parser.getStringValue();
            int end = 3 > initials.length() ? initials.length() : 3;
            initials = initials.substring(0, end);
            parser.setOffset(5);
            int year = parser.getIntValue(FORMAT_UINT16);
            int month = parser.getIntValue(FORMAT_UINT8);
            int day = parser.getIntValue(FORMAT_UINT8);
            int height = parser.getIntValue(FORMAT_UINT8);
            int gender = parser.getIntValue(FORMAT_UINT8);
            int activityLevel = parser.getIntValue(FORMAT_UINT8);
            GregorianCalendar calendar = new GregorianCalendar(year, month - 1, day);
            ScaleUser scaleUser = new ScaleUser(initials, calendar.getTime(), height, gender, activityLevel - 1);
            scaleUser.setId(index);
            scaleUserList.add(scaleUser);
            if (scaleUserList.size() == BF600_MAX_USERS) {
                chooseExistingScaleUser(scaleUserList);
            }
        }
        else {
            super.onBluetoothNotify(characteristic, value);
        }
    }

    protected void chooseExistingScaleUser(Vector<ScaleUser> userList) {
        final DateFormat dateFormat = DateFormat.getDateInstance();
        int choicesCount = userList.size();
        if (userList.size() < BF600_MAX_USERS) {
            choicesCount = userList.size() + 1;
        }
        CharSequence[] choiceStrings = new String[choicesCount];
        int indexArray[] = new int[choicesCount];
        int selectedItem = -1;
        for (int i = 0; i < userList.size(); ++i) {
            ScaleUser u = userList.get(i);
            choiceStrings[i] = "P-0" + u.getId()
                    + " " + (u.getGender().isMale() ? "male" : "female")
                    + " " + "height:" + u.getBodyHeight()
                    + " birthday:" + dateFormat.format(u.getBirthday())
                    + " " + "AL:" + (u.getActivityLevel().toInt() + 1);
            indexArray[i] = u.getId();
        }
        if (userList.size() < BF600_MAX_USERS) {
            choiceStrings[userList.size()] = "Create new user on scale.";
            indexArray[userList.size()] = -1;
        }
        Pair<CharSequence[], int[]> choices = new Pair(choiceStrings, indexArray);
        chooseScaleUserUi(choices);
    }
}
