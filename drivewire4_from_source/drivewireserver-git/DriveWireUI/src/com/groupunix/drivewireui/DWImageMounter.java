/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DriveWireUI.src.com.groupunix.drivewireui;

import com.groupunix.drivewireui.MainWin;
import com.groupunix.drivewireui.SendCommandWin;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.VFS;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

/**
 *
 * @author pedro
 */
public class DWImageMounter extends Dialog {

    private boolean appendDOSFile = false;
    private final Display display;
    private final int driveNumber;

    public DWImageMounter(Shell parent, boolean appendMode, int driveNum) {
        super(parent);

        appendDOSFile = appendMode;
        display = parent.getDisplay();
        driveNumber = driveNum;

    }

    public void mountDisks(String url) {

        if (url == null) {
            return;
        }

        String filename = getFilename(url);

        if (filename == null) {
            return;
        }

        String extension = getExtension(filename);

        if (extension == null) {
            return;
        }

        String supportedExtensions = this.getSupportedExtensions();
        // handle archives
        if (supportedExtensions.contains(extension)) {
            //FileObject fileobj;
            try {

                String[] allFileUris = new String[1];
                int numberOfFiles = 1;


                if (extension.toLowerCase().equals("zip")) {

                    allFileUris = findAllFileUris(url);
                    numberOfFiles = allFileUris.length;

                } else if (numberOfFiles == 1) {

                    allFileUris[0] = url;

                }//end else if
                //String fileNameURI;

                int rc = SWT.YES;
                if (numberOfFiles > 1) {

                    // more than one dsk.. prompt
                    MessageBox messageBox = new MessageBox(MainWin.getMainShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.APPLICATION_MODAL);
                    messageBox.setMessage("There are " + numberOfFiles + " images in this archive.  Would you like to load them into drives " + driveNumber + " through " + (driveNumber + numberOfFiles - 1) + "?");
                    messageBox.setText("Multiple disk images found");
                    rc = messageBox.open();
                }
                if (rc == SWT.YES) {

                    List<String> cmds = new ArrayList<String>();
                    int spinnerOffset = 0;

                    //adding the files to dw.
                    for (String uri : allFileUris) {

                            // String fileNameURI = findFirstFileUri(uri);
                            String ext = this.getExtension(uri);

                            if (isExtension("Disk", ext)) {

                                cmds.add("dw disk insert " + (driveNumber + spinnerOffset) + " " + uri);
                                spinnerOffset++;

                            }

                            if (isExtension("DOS", ext) && !appendDOSFile) {
                                cmds.add("dw disk create " + (driveNumber + spinnerOffset));
                                cmds.add("dw disk dos format " + (driveNumber + spinnerOffset));
                                cmds.add("dw disk dos add " + (driveNumber + spinnerOffset) + " " + uri);
                                spinnerOffset++;

                            }
                            
                            if (isExtension("DOS", ext) && appendDOSFile) {
                                cmds.add("dw disk dos add " + (driveNumber + spinnerOffset) + " " + uri);
                                
                            }                            

                        }//end for

                        sendCommandDialog(cmds, "Loading disk image..", "Please wait while the server loads the disk image.");

                    }//end if



            } catch (FileSystemException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else {
            showError("No usable files found", "The archive does not contain any files with known extensions.");
        }


    }// end doCoCoLink

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

    public String getSupportedExtensions() {
        String extensionFilter = "";
        List<String> extensions = new ArrayList();
        List<String> DiskExtensions = MainWin.config.getList("DiskExtensions");
        List<String> DOSExtensions = MainWin.config.getList("DOSExtensions");
        List<String> OS9Extensions = MainWin.config.getList("OS9Extensions");
        List<String> ArchiveExtensions = MainWin.config.getList("ArchiveExtensions");

        extensions.addAll(OS9Extensions);
        extensions.addAll(DiskExtensions);
        extensions.addAll(DOSExtensions);
        extensions.addAll(ArchiveExtensions);
        extensions.addAll(ArchiveExtensions);

        for (String extension : extensions) {
            extensionFilter += "*." + extension.toLowerCase() + ";" + "*." + extension.toUpperCase() + ";";

        }
        //extensionFilter += "*.*";
        return extensionFilter;
    }

    /**
     * Traverses directories in a zip file and returns the URIs of all files
     * found
     *
     * @param zipFilePath The path to the zip file (e.g., "/path/to/file.zip")
     * @return An array of URIs for all files found in the zip
     * @throws FileSystemException if there's an error accessing the zip file
     */
    public String[] findAllFileUris(String zipFilePath) throws FileSystemException {
        FileSystemManager fsManager = VFS.getManager();

        // Create the zip file URI - format: zip:file:///path/to/file.zip
        String zipUri = "zip:" + zipFilePath;

        FileObject zipFile = fsManager.resolveFile(zipUri);

        try {
            List<String> fileUris = new ArrayList<String>();
            // Start traversal from the root of the zip
            collectAllFiles(zipFile, fileUris);
            return fileUris.toArray(new String[0]);
        } finally {
            // Clean up
            zipFile.close();
        }
    }

    /**
     * Recursively collects all file URIs
     *
     * @param fileObject The current file object to process
     * @param fileUris List to collect file URIs
     * @throws FileSystemException if there's an error accessing files
     */
    private void collectAllFiles(FileObject fileObject, List<String> fileUris)
            throws FileSystemException {

        boolean suportedType = this.isCoCoExtension(getExtension(fileObject.getName().getURI()));

        // Check if this is a file
        if (fileObject.getType() == FileType.FILE && suportedType) {
            fileUris.add(fileObject.getName().getURI().replaceAll("\\s", "%20"));
        }

        // If it's a directory, search its children
        if (fileObject.getType() == FileType.FOLDER) {
            FileObject[] children = fileObject.getChildren();

            if (children != null) {
                for (FileObject child : children) {
                    collectAllFiles(child, fileUris);
                }
            }
        }
    }

    private void showError(String title, String msg) {
        MessageBox messageBox = new MessageBox(MainWin.getMainShell(), SWT.ICON_ERROR | SWT.OK);
        messageBox.setMessage(msg);
        messageBox.setText(title);
        messageBox.open();
    }

    protected void sendCommandDialog(final List<String> cmd, final String title, final String message) {
        final Shell shell = MainWin.getMainShell();

        display.asyncExec(
                new Runnable() {
            public void run() {

                SendCommandWin win = new SendCommandWin(shell, SWT.DIALOG_TRIM, cmd, title, message);

                win.open();

            }
        });

    }

}// end class
