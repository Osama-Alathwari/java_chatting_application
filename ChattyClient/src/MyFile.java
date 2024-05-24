/**
 *
 * @author root
 */

//  A Class that Represent each Recieved File , Which is each one of our pannels in the GUI
public class MyFile {
    // An id to represent the File
    private int id;
    // The name of the File
    private String name;
    // The Actual Data of the File
    private byte[] data;
    // The Extention of the File
    private String fileExtention;

    public MyFile(int id, String name, byte[] data, String fileExtention) {
        this.id = id;
        this.name = name;
        this.data = data;
        this.fileExtention = fileExtention;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setFileExtention(String fileExtention) {
        this.fileExtention = fileExtention;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public byte[] getData() {
        return data;
    }

    public String getFileExtention() {
        return fileExtention;
    }

}
