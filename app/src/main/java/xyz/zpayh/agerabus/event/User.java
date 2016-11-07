package xyz.zpayh.agerabus.event;

/**
 * 文 件 名: User
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/11/7 22:32
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */

public class User {

    private String mName;

    public User(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
}
