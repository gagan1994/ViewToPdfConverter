package com.example.generatepdfwithview;

import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

public class ViewToPdfConverter {

    public static class ViewToPdfBuilder {
        private String PathToStore;
        private String FileNameWithoutExt;

        public String getFileNameWithoutExt() {
            return FileNameWithoutExt;
        }

        public ViewToPdfBuilder setFileNameWithoutExt(String fileNameWithoutExt) {
            FileNameWithoutExt = fileNameWithoutExt;
            return this;
        }


        public String getPathToStore() {
            return PathToStore;
        }

        public ViewToPdfBuilder setPathToStore(String pathToStore) {
            PathToStore = pathToStore;
            return this;
        }

        public ViewToPdfConverter build() {
            ViewToPdfConverter converter = new ViewToPdfConverter(this);
            return converter;
        }
    }

    private String PathToStore = "file";
    private String FileNameWithoutExt = "created_file.pdf";
    private File pdfDir;

    public File getFilePath() {
        return pdfDir;
    }

    public String getFileName() {
        return FileNameWithoutExt;
    }

    public ViewToPdfConverter(@NonNull ViewToPdfBuilder builder) {
        this.PathToStore = (builder.getPathToStore() == null
                || builder.getPathToStore().trim().length() == 0) ?
                PathToStore :
                builder.getPathToStore();
        this.FileNameWithoutExt = (builder.getFileNameWithoutExt() == null
                || builder.getFileNameWithoutExt().trim().length() == 0) ?
                FileNameWithoutExt : builder.getFileNameWithoutExt() + ".pdf";
    }

    public boolean generatePdf(View view) {
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            Toast.makeText(view.getContext(), "not mounted", Toast.LENGTH_SHORT).show();
        }

//Create a directory for your PDF
        pdfDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), PathToStore);
        if (!pdfDir.exists()) {
            pdfDir.mkdir();
        }

        view.setDrawingCacheEnabled(true);
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();

//Then take the screen shot
        Bitmap screen;

        screen = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

//Now create the name of your PDF file that you will generate
        File pdfFile = new File(pdfDir, FileNameWithoutExt);
        try {
            Document document = new Document();

            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            screen.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            addImage(document, byteArray);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static void addImage(Document document, byte[] byteArray) {
        Image image = null;
        try {
            image = Image.getInstance(byteArray);
        } catch (BadElementException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // image.scaleAbsolute(150f, 150f);
        try {
            document.add(image);
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
