
// ...

private volatile int progressImageIndex = 0;
private Button cancelButton;
private PaintListener paintListener;
private Color shadowColor;
private String progressMessage;
private long loadStartTime;

// ...

private void showProgress() {
    if (loadStartTime == 0) {
        return;
    }
    if (progressOverlay == null) {
        // Start progress visualization
        // ...
    }
    progressImageIndex++;
    if (progressOverlay != null) {
        progressOverlay.layout();
        progressPane.redraw();
    }
}

// ...

        cancelButton = new Button(progressPane, SWT.PUSH);
        // ...
        paintListener = e -> {
            // ...
        };
        progressPane.addPaintListener(paintListener);

// ...
