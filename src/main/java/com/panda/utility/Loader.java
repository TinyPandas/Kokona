package com.panda.utility;

public abstract class Loader {
    public abstract <T> T registerClass(Class<T> commandClass);
}
