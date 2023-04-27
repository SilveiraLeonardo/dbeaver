
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// ...

private void writeImageCell(File file) throws DBException {
    PrintWriter out = getWriter();
    out.write("<td>");
    if (file == null || !file.exists()) {
        out.write("&nbsp;");
    } else {
        BufferedImage image = null;
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            throw new DBException("Can't read an exported image " + image, e);
        }

        if (image != null) {
            Path basePath = Paths.get("files");
            Path imagePath = file.toPath().normalize();
            if (!imagePath.startsWith(basePath)) {
                // This means the file is trying to access outside of the "files" folder
                throw new SecurityException("Invalid file access, file path is not allowed: " + imagePath);
            }
                       
            String imagePathStr = imagePath.toString();
            imagePathStr = "files/" + imagePathStr.substring(imagePathStr.lastIndexOf(File.separator));

            int width = image.getWidth();
            int height = image.getHeight();
            int rwidth = width;
            int rheight = height;

            if (width > IMAGE_FRAME_SIZE || height > IMAGE_FRAME_SIZE) {
                float scale;
                if (width > height) {
                    scale = IMAGE_FRAME_SIZE / (float) width;
                } else {
                    scale = IMAGE_FRAME_SIZE / (float) height;
                }
                rwidth = (int) (rwidth * scale);
                rheight = (int) (rheight * scale);
            }
            out.write("<a href=\"" + imagePathStr + "\">");
            out.write("<img src=\"" + imagePathStr + "\" width=\"" + rwidth + "\" height=\"" + rheight + "\" />");
            out.write("</a>");
        } else {
            out.write("&nbsp;");
        }
    }
    out.write("</td>");
}
