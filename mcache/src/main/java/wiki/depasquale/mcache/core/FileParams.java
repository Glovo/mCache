package wiki.depasquale.mcache.core;

/**
 * Created by diareuse on 01/06/2017. Yeah. Suck it.
 */

public class FileParams<T> {

  public static final int MATCHING_DES_ID = 0x0101;
  private static final String error = "\nThis operation could have corrupted the file.";
  public static int MATCHING = 0x1111;
  private long id = -1;
  private long timeCreated = -1;
  private long timeChanged = -1;
  private Class<T> fileClass = null;
  private CharSequence descriptor = null;

  public static int compare(FileParams<?> fp1, FileParams<?> fp2) {
    int result = 0x0;
    if (fp1.id == fp2.id) {
      result += 0x1;
    }
    if (fp1.timeCreated == fp2.timeCreated) {
      result += 0x10;
    }
    if (equals(fp1.descriptor, fp2.descriptor)) {
      result += 0x100;
    }
    if (fp1.timeChanged == fp2.timeChanged) {
      result += 0x1000;
    }
    return result;
  }

  private static boolean equals(Object o1, Object o2) {
    return !(o1 == null && o2 == null) && o1 != null && (o1 == o2 || o1.equals(o2));
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    if (this.id == -1) { this.id = id; } else {
      throw new UnsupportedOperationException("You may not change id." + error);
    }
  }

  void internalForceSetId(long id) {
    this.id = id;
  }

  public long getTimeCreated() {
    return timeCreated;
  }

  public void setTimeCreated(long timeCreated) {
    if (this.timeCreated == -1) { this.timeCreated = timeCreated; } else {
      throw new UnsupportedOperationException("You may not change created time." + error);
    }
  }

  public long getTimeChanged() {
    return timeChanged;
  }

  public void setTimeChanged(long timeChanged) {
    this.timeChanged = timeChanged;
  }

  public Class<T> getFileClass() {
    return fileClass;
  }

  public void setFileClass(Class<T> fileClass) {
    if (this.fileClass == null) { this.fileClass = fileClass; } else {
      throw new UnsupportedOperationException("You may not change class." + error);
    }
  }

  public CharSequence getDescriptor() {
    return descriptor;
  }

  public void setDescriptor(CharSequence descriptor) {
    if (this.descriptor == null) { this.descriptor = descriptor; } else {
      throw new UnsupportedOperationException("You may not change descriptor." + error);
    }
  }
}
