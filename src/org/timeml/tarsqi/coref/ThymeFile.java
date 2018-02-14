package org.timeml.tarsqi.coref;

import java.io.File;

public class ThymeFile {
    
    File file = null;
    String text = null;

    public ThymeFile(File file, String text) {
        this.file = file;
        this.text = text;
    }
    
    @Override
    public String toString() {
        return String.format("<ThymeFile %s>", this.file.getName());
    }

}
