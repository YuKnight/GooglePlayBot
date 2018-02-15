package com.googleplaybot.models;

public class TCP {

	public long uid;

	public long bytes;

	public TCP(long uid, long bytes) {
		this.uid = uid;
		this.bytes = bytes;
	}

    @Override
    public String toString() {
        return "TCP{" +
                "uid=" + uid +
                ", bytes=" + bytes +
                '}';
    }
}