
public void paint(PaintEvent e) {
    // ...
    if (!CommonUtils.isEmpty(text)) {
        // Offset shift. Calculate device zoom for Windows only (14048)
        boolean shiftOffset = false;
        if (RuntimeUtils.isWindows()) {
            int deviceDPI = e.display.getDPI().x;
            int defaultDPI = 96; // Default DPI for most displays
            double deviceZoom = (double) deviceDPI / defaultDPI * 100;
            shiftOffset = (deviceZoom >= 200);
        }

        transform = new Transform(e.display);

        e.gc.setAntialias(SWT.ON);
        if ((getStyle() & SWT.RIGHT) == SWT.RIGHT) {
            transform.translate(size.x, 0);
            transform.rotate(90);
            if (shiftOffset) {
                yOffset -= size.x / 2;
            }
        } else {
            transform.translate(0, size.y);
            transform.rotate(-90);
            if (shiftOffset) {
                xOffset -= size.y / 2;
            }
        }
        e.gc.setTransform(transform);

        xOffset += VERT_INDENT;
    }
    // ...
}
