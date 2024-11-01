package com.doudoudrive.auth.shiro;

import org.apache.shiro.session.mgt.SimpleSession;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * <p>自定义ShiroSession实现，重写原有默认SimpleSession内容</p>
 * <p>2022-04-16 23:55</p>
 *
 * @author Dan
 **/
public class ShiroSession extends SimpleSession implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 除lastAccessTime以外其他字段发生改变时为true
     */
    private boolean isChanged = false;

    public ShiroSession() {
        super();
        this.setChanged(true);
    }

    public ShiroSession(String host) {
        super(host);
        this.setChanged(true);
    }

    @Override
    public void setId(Serializable id) {
        super.setId(id);
        this.setChanged(true);
    }

    @Override
    public Date getStartTimestamp() {
        return super.getStartTimestamp();
    }

    @Override
    public void setStartTimestamp(Date startTimestamp) {
        super.setStartTimestamp(startTimestamp);
        this.setChanged(false);
    }

    @Override
    public Date getStopTimestamp() {
        return super.getStopTimestamp();
    }

    @Override
    public void setStopTimestamp(Date stopTimestamp) {
        super.setStopTimestamp(stopTimestamp);
        this.setChanged(true);
    }

    @Override
    public Date getLastAccessTime() {
        return super.getLastAccessTime();
    }

    @Override
    public void setLastAccessTime(Date lastAccessTime) {
        super.setLastAccessTime(lastAccessTime);
        this.setChanged(false);
    }

    @Override
    public void setTimeout(long timeout) {
        super.setTimeout(timeout);
        this.setChanged(true);
    }

    @Override
    public void setExpired(boolean expired) {
        super.setExpired(expired);
        this.setChanged(true);
    }

    @Override
    public void setHost(String host) {
        super.setHost(host);
        this.setChanged(true);
    }

    @Override
    public void setAttributes(Map<Object, Object> attributes) {
        super.setAttributes(attributes);
        this.setChanged(true);
    }

    @Override
    public void setAttribute(Object key, Object value) {
        super.setAttribute(key, value);
        this.setChanged(true);
    }

    @Override
    public Object removeAttribute(Object key) {
        this.setChanged(true);
        return super.removeAttribute(key);
    }

    /**
     * 停止
     */
    @Override
    public void stop() {
        super.stop();
        this.setChanged(true);
    }

    /**
     * 设置过期
     */
    @Override
    protected void expire() {
        this.stop();
        this.setExpired(true);
    }

    public boolean isChanged() {
        return isChanged;
    }

    public void setChanged(boolean isChanged) {
        this.isChanged = isChanged;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    protected boolean onEquals(SimpleSession ss) {
        return super.onEquals(ss);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
