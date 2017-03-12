package xyz.zpayh.agerabus.event;

import java.io.Serializable;
import java.util.Date;

/**
 * 文 件 名: RemoteUser
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/11/22 23:26
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */

public class RemoteUser implements Serializable{
    private static final long serialVersionUID = 10086L;

    public final String mName;

    public final Date mDate;

    public RemoteUser(String name,Date date) {
        mName = name;
        mDate = new Date(date.getTime());
    }
}
