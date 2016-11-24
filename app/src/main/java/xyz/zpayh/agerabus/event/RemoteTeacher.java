package xyz.zpayh.agerabus.event;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * 文 件 名: RemoteTeacher
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/11/24 21:42
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */

public class RemoteTeacher implements Parcelable {

    private Date mBirthDay;

    private String mName;

    public RemoteTeacher(Date birthDay, String name) {
        mBirthDay = birthDay;
        mName = name;
    }

    public Date getBirthDay() {
        return mBirthDay;
    }

    public void setBirthDay(Date birthDay) {
        mBirthDay = birthDay;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mBirthDay != null ? this.mBirthDay.getTime() : -1);
        dest.writeString(this.mName);
    }

    protected RemoteTeacher(Parcel in) {
        long tmpMBirthDay = in.readLong();
        this.mBirthDay = tmpMBirthDay == -1 ? null : new Date(tmpMBirthDay);
        this.mName = in.readString();
    }

    public static final Parcelable.Creator<RemoteTeacher> CREATOR = new Parcelable.Creator<RemoteTeacher>() {
        @Override
        public RemoteTeacher createFromParcel(Parcel source) {
            return new RemoteTeacher(source);
        }

        @Override
        public RemoteTeacher[] newArray(int size) {
            return new RemoteTeacher[size];
        }
    };
}
