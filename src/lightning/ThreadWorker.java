package lightning;

import javax.swing.*;

/**
 * Divides work to threads (drawing on background)
 */
class ThreadWorker extends SwingWorker<Void, Void>
{
    int iteration;
    Sin sinPaint;

    public ThreadWorker(int iteration, Sin sinPaint) {
        this.iteration = iteration;
        this.sinPaint = sinPaint;
    }

    protected Void doInBackground() throws Exception
    {
        sinPaint.draw(iteration);
        return null;
    }

    protected void done()
    {
    }
}