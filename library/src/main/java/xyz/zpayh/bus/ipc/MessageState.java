package xyz.zpayh.bus.ipc;

import android.support.annotation.IntDef;

/**
 * 文 件 名: MessageState
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/11/22 22:26
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */
@IntDef({MessageState.MSG_FROM_CLIENT,MessageState.MSG_FROM_EVENT})
public @interface MessageState {
    int MSG_FROM_CLIENT = 0;
    int MSG_FROM_EVENT = 1;
}
