package com.groupunix.drivewireui.plugin;

import java.net.URL;
import java.net.URLClassLoader;

public class DWClassLoader extends URLClassLoader {

    public DWClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public void addURL(URL url) {
        super.addURL(url);
    }
}
