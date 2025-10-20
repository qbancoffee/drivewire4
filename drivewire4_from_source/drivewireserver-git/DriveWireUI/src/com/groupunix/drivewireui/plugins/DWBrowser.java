package com.groupunix.drivewireui.plugins;

import DriveWireUI.src.com.groupunix.drivewireui.DWImageMounter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.VFS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.SWTResourceManager;

import swing2swt.layout.BorderLayout;

import com.groupunix.drivewireui.GradientHelper;
import com.groupunix.drivewireui.MainWin;
import com.groupunix.drivewireui.SendCommandWin;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FileType;
import org.eclipse.swt.widgets.Button;

public class DWBrowser extends Composite {

    public final static int LTYPE_LOCAL_ROOT = 0;
    public final static int LTYPE_LOCAL_FOLDER = 1;
    public final static int LTYPE_LOCAL_ENTRY = 2;
    public final static int LTYPE_NET_ROOT = 10;
    public final static int LTYPE_NET_FOLDER = 11;
    public final static int LTYPE_NET_ENTRY = 12;
    public final static int LTYPE_CLOUD_ROOT = 20;
    public final static int LTYPE_CLOUD_FOLDER = 21;
    public final static int LTYPE_CLOUD_ENTRY = 22;

    private Browser browser;
    private Composite header;

    private ToolItem tltmBack;
    private ToolItem tltmForward;
    private ToolItem tltmReload;
    private Combo comboURL;
    public static Spinner spinnerDrive;

    private CTabItem ourtab;
    private Cursor handcursor;
    private Cursor normcursor;
    private ToolBar toolBar_1;
    private ToolItem toolBookmarks;
    private ToolBar toolBar_2;
    private ToolItem toolDrive;

    //public boolean append_mode = MainWin.append_mode;

    public DWBrowser(final Composite parent, String url, final CTabItem ourtab) {
        super(parent, SWT.BORDER);
        this.setOurtab(ourtab);
        //this.append_mode=MainWin.append_mode;

        handcursor = new Cursor(MainWin.getDisplay(), SWT.CURSOR_HAND);
        setNormcursor(new Cursor(MainWin.getDisplay(), SWT.CURSOR_ARROW));

        setLayout(new BorderLayout(0, 0));

        //setBounds(comp.getBounds());
        header = new Composite(this, SWT.NONE);
        header.setLayoutData(BorderLayout.NORTH);
        header.setLayout(new FormLayout());

        GradientHelper.applyVerticalGradientBG(header, MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT), MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND));

        header.addListener(SWT.Resize, new Listener() {

            @Override
            public void handleEvent(Event event) {
                GradientHelper.applyVerticalGradientBG(header, MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT), MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND));

            }
        });

        header.setBackgroundMode(SWT.INHERIT_FORCE);

        ToolBar toolBar = new ToolBar(header, SWT.FLAT | SWT.RIGHT);

        FormData fd_toolBar = new FormData();
        fd_toolBar.left = new FormAttachment(0, 10);
        fd_toolBar.top = new FormAttachment(0, 8);
        toolBar.setLayoutData(fd_toolBar);

        tltmBack = new ToolItem(toolBar, SWT.NONE);
        tltmBack.setToolTipText("Back");
        tltmBack.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                browser.back();
            }
        });
        tltmBack.setWidth(30);
        tltmBack.setImage(SWTResourceManager.getImage(DWBrowser.class, "/toolbar/arrow-left-3.png"));

        tltmForward = new ToolItem(toolBar, SWT.NONE);
        tltmForward.setToolTipText("Forward");
        tltmForward.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                browser.forward();
            }
        });
        tltmForward.setWidth(30);
        tltmForward.setImage(SWTResourceManager.getImage(DWBrowser.class, "/toolbar/arrow-right-3.png"));

        tltmReload = new ToolItem(toolBar, SWT.NONE);
        tltmReload.setToolTipText("Reload page");
        tltmReload.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                browser.refresh();
            }
        });
        tltmReload.setWidth(30);
        tltmReload.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/view-refresh-7.png"));

        // Create help label first so we can reference it
        final Label lblHelp = new Label(header, SWT.NONE);
        lblHelp.setToolTipText("Show browser help");
        lblHelp.setCursor(handcursor);
        lblHelp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent arg0) {
                browser.setUrl(MainWin.config.getString("Browser_helppage", "https://github.com/qbancoffee/drivewire4/wiki/DriveWire-4-Help"));
            }
        });
        lblHelp.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/help-about-3.png"));
        FormData fd_lblHelp = new FormData();
        fd_lblHelp.right = new FormAttachment(100, -10);
        fd_lblHelp.top = new FormAttachment(0, 8);
        lblHelp.setLayoutData(fd_lblHelp);

        // Create spinner with fixed width, attached to help label
        spinnerDrive = new Spinner(header, SWT.BORDER);
        spinnerDrive.setBackground(new Color(MainWin.getDisplay(), 255, 255, 255));
        spinnerDrive.setToolTipText("Working drive");
        FormData fd_spinnerDrive = new FormData();
        fd_spinnerDrive.right = new FormAttachment(lblHelp, -10, SWT.LEFT);
        fd_spinnerDrive.width = 60;
        fd_spinnerDrive.top = new FormAttachment(0, 8);
        spinnerDrive.setLayoutData(fd_spinnerDrive);
        spinnerDrive.setMinimum(0);
        spinnerDrive.setMaximum(255);
        spinnerDrive.setSelection(0);

        // Create toolBar_2, attached to left of spinner
        toolBar_2 = new ToolBar(header, SWT.FLAT | SWT.RIGHT);
        FormData fd_toolBar_2 = new FormData();
        fd_toolBar_2.right = new FormAttachment(spinnerDrive, -6);
        fd_toolBar_2.top = new FormAttachment(0, 8);
        toolBar_2.setLayoutData(fd_toolBar_2);

        spinnerDrive.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                // Code to execute when the spinner value changes
                int selectedValue = spinnerDrive.getSelection();
                MainWin.table.setSelection(selectedValue);

            }
        });

        
        
        //add actionlister to spinnerDrive
        
        toolDrive = new ToolItem(toolBar_2, SWT.NONE);
        toolDrive.setToolTipText("Toggle append mode");
        toolDrive.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                if (MainWin.append_mode) {
                    toolDrive.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/disk-insert.png"));
                    MainWin.append_mode = false;
                } else {
                    toolDrive.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/new-disk-16.png"));
                    MainWin.append_mode = true;
                }
            }
        });
        toolDrive.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/disk-insert.png"));

        // Create toolBar_1, attached to left of toolBar_2
        toolBar_1 = new ToolBar(header, SWT.FLAT | SWT.RIGHT);
        FormData fd_toolBar_1 = new FormData();
        fd_toolBar_1.right = new FormAttachment(toolBar_2, -6);
        fd_toolBar_1.top = new FormAttachment(0, 8);
        toolBar_1.setLayoutData(fd_toolBar_1);

//        toolBookmarks = new ToolItem(toolBar_1, SWT.NONE);
//        toolBookmarks.addSelectionListener(new SelectionAdapter() {
//            @Override
//            public void widgetSelected(SelectionEvent arg0) {
//                if (isBookmark(comboURL.getText())) {
//                    removeBookmark(comboURL.getText());
//                } else {
//                    addBookmark(comboURL.getText());
//                }
//            }
//        });
//        toolBookmarks.setToolTipText("Add/Remove bookmark");
//        toolBookmarks.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/bookmark-new-2.png"));
//        toolBookmarks.setEnabled(false);

        // Create combo URL, attached between left toolbar and right toolbar_1
        comboURL = new Combo(header, SWT.NONE);
        comboURL.setToolTipText("");
        fd_toolBar.right = new FormAttachment(comboURL, -6);
        comboURL.setBackground(new Color(MainWin.getDisplay(), 255, 255, 255));
        comboURL.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                toggleBookmarkIcon(comboURL.getText());

                if (e.keyCode == 13) {
                    browser.setUrl(comboURL.getText());
                } else if ((e.keyCode == 16777217) || (e.keyCode == 16777218)) {
                    e.doit = false;
                }
            }
        });
        comboURL.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                browser.setUrl(comboURL.getText());
            }
        });

        FormData fd_comboURL = new FormData();
        fd_comboURL.left = new FormAttachment(0, 90);
        fd_comboURL.right = new FormAttachment(toolBar_1, -6);
        fd_comboURL.bottom = new FormAttachment(100, -5);
        fd_comboURL.top = new FormAttachment(2, 8);
        comboURL.setLayoutData(fd_comboURL);

        browser = null;

        UncaughtExceptionHandler uncex = Thread.currentThread().getUncaughtExceptionHandler();

        Thread.currentThread().setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread t, final Throwable e) {

                System.out.println("It seems we cannot open a browser window on this system.");
                System.out.println();
                System.out.println("The error message is: " + e.getMessage());
                System.out.println();

                MainWin.config.setProperty("NoBrowsers", true);

                System.out.println("I've disabled opening browsers in the configuration.  You will have to restart DriveWire.");
                System.exit(1);
            }

        });

        browser = new Browser(this, SWT.NONE);

        Thread.currentThread().setUncaughtExceptionHandler(uncex);

        browser.addLocationListener(new LocationAdapter() {
            @Override
            public void changed(LocationEvent event) {

                comboURL.setText(event.location);

                toggleBookmarkIcon(event.location);

                if (browser.isBackEnabled()) {
                    tltmBack.setEnabled(true);
                } else {
                    tltmBack.setEnabled(false);
                }

                if (browser.isForwardEnabled()) {
                    tltmForward.setEnabled(true);
                } else {
                    tltmForward.setEnabled(false);
                }

                tltmReload.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/view-refresh-7.png"));

            }

            @Override
            public void changing(LocationEvent event) {
                if (isCocoLink(event.location)) {
                    event.doit = false;

                    doCoCoLink(event.location);
                }
            }

        });

        browser.addTitleListener(new TitleListener() {
            public void changed(TitleEvent event) {
                ourtab.setText(event.title);
                ourtab.setImage(SWTResourceManager.getImage(MainWin.class, "/menu/www.png"));

            }
        });

        browser.addProgressListener(new ProgressListener() {
            public void changed(ProgressEvent event) {

                if (event.total == 0) {
                    return;
                }

                tltmReload.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/network.png"));

            }

            public void completed(ProgressEvent event) {

                tltmReload.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/view-refresh-7.png"));

            }

        });

        if (url != null) {
            loadBookmarkList();
            browser.setUrl(url);
        } else {
            // browser.setUrl(MainWin.config.getString("Browser_homepage", "https://github.com/qbancoffee/drivewire4/wiki/DriveWire-4-Cloud") );
        }

    }

    protected void toggleBookmarkIcon(String url) {
//        if (isBookmark(url)) {
//            toolBookmarks.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/bookmark-del-2.png"));
//        } else {
//            toolBookmarks.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/bookmark-new-2.png"));
//        }

    }

    @SuppressWarnings("unchecked")
    protected void addBookmark(String url) {

        List<String> bm = MainWin.config.getList("Bookmark");

        if (!bm.contains(url)) {
            MainWin.config.addProperty("Bookmark", url);
        }

        toggleBookmarkIcon(url);

        String tmp = comboURL.getText();
        comboURL.removeAll();

        bm = MainWin.config.getList("Bookmark");

        for (String b : bm) {
            comboURL.add(b);
        }

        comboURL.setText(tmp);
    }

    protected void removeBookmark(String url) {
        int bms = MainWin.config.getMaxIndex("Bookmark");

        for (int x = 0; x <= bms; x++) {
            if (MainWin.config.getString("Bookmark(" + x + ")").equalsIgnoreCase(url)) {
                MainWin.config.clearProperty("Bookmark(" + x + ")");

                break;
            }
        }

        toggleBookmarkIcon(url);

        loadBookmarkList();
    }

    @SuppressWarnings("unchecked")
    private void loadBookmarkList() {
        String tmp = comboURL.getText();
        comboURL.removeAll();

        List<String> bm = MainWin.config.getList("Bookmark");

        for (String b : bm) {
            comboURL.add(b);
        }

        comboURL.setText(tmp);
    }

    @SuppressWarnings("unchecked")
    protected boolean isBookmark(String url) {
        List<String> bm = MainWin.config.getList("Bookmark");

        if (bm.contains(url)) {
            return (true);
        }

        return false;
    }

    public void openURL(String url) {

        browser.setUrl(url);

    }

    protected void doCoCoLink(String url) {
        DWImageMounter dWDiskMounter = new DWImageMounter(MainWin.getMainShell(), MainWin.append_mode,MainWin.sdisk);
        dWDiskMounter.mountDisks(url);
       
    }

    private void showError(String title, String msg) {
        MessageBox messageBox = new MessageBox(MainWin.getMainShell(), SWT.ICON_ERROR | SWT.OK);
        messageBox.setMessage(msg);
        messageBox.setText(title);
        messageBox.open();
    }

    protected boolean isCocoLink(String location) {
        String filename = getFilename(location);

        if (filename != null) {
            String extension = getExtension(filename);

            if ((extension != null) && isCoCoExtension(extension)) {
                return (true);
            }

        }

        return false;
    }

    private String getExtension(String filename) {

        if (filename == null) {
            return null;
        }

        String ext = null;

        int lastdot = filename.lastIndexOf('.');

        // extension?
        if ((lastdot > 0) && (lastdot < filename.length() - 2)) {
            ext = filename.substring(lastdot + 1);
        }

        return ext;
    }

    private String getFilename(String location) {

        if (location == null) {
            return null;
        }

        String filename = null;

        int lastslash = location.lastIndexOf('/');

        // parse up url
        if ((lastslash > 0) && (lastslash < location.length() - 2)) {
            filename = location.substring(lastslash + 1);
        }

        return filename;
    }

    private boolean isCoCoExtension(String ext) {
        if (ext == null) {
            return false;
        }

        if (isExtension("Disk", ext)) {
            return true;
        }

        if (isExtension("DOS", ext)) {
            return true;
        }

        if (isExtension("OS9", ext)) {
            return true;
        }

        if (isExtension("Archive", ext)) {
            return true;
        }

        return false;
    }

    private boolean isExtension(String exttype, String ext) {
        if (exttype == null || ext == null) {
            return false;
        }

        if (MainWin.config.containsKey(exttype + "Extensions")) {
            @SuppressWarnings("unchecked")
            List<String> exts = MainWin.config.getList(exttype + "Extensions");

            if (exts.contains(ext.toLowerCase())) {
                return (true);
            }
        }
        return false;
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }



    public CTabItem getOurtab() {
        return ourtab;
    }

    public void setOurtab(CTabItem ourtab) {
        this.ourtab = ourtab;
    }

    public Cursor getNormcursor() {
        return normcursor;
    }

    public void setNormcursor(Cursor normcursor) {
        this.normcursor = normcursor;
    }



    public Spinner getSpinner() {
        return spinnerDrive;
    }

    public Browser getBrowser() {
        return browser;
    }

    public boolean getAppendMode() {
        return MainWin.append_mode;
    }

//    public Display getDisplay(){
//    return display;
//    }
}// end class
