package com.chaojishipin.sarrs.fragment.videoplayer;

import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;

/**
 * 用于刷新播放器的计时器
 */
public class PlayerTimer extends TimerTask{
	
	int m_iMsg = 0;

    Handler handler = null;

    public PlayerTimer(Handler handler, int iMsg) {
        m_iMsg = iMsg;
        this.handler = handler;
    }

    public void run() {
        Message msg = new Message();
        msg.what = m_iMsg;
        handler.sendMessage(msg);
    }

}
