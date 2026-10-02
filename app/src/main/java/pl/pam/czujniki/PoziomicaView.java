package pl.pam.czujniki;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/** Rysuje okrągłą poziomicę z bąbelkiem przesuwanym wg przyspieszenia. */
public class PoziomicaView extends View {

    private static final float MAX_G = 9.81f;

    private final Paint tlo = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint linie = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint babel = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint tekst = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float ax, ay;

    public PoziomicaView(Context context) {
        super(context);
        tlo.setColor(Color.rgb(200, 230, 120));
        linie.setColor(Color.DKGRAY);
        linie.setStyle(Paint.Style.STROKE);
        linie.setStrokeWidth(4f);
        tekst.setColor(Color.BLACK);
        tekst.setTextSize(48f);
        tekst.setTextAlign(Paint.Align.CENTER);
    }

    public void ustaw(float ax, float ay) {
        this.ax = ax;
        this.ay = ay;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        float r = Math.min(cx, cy) * 0.8f;
        float rBabla = r * 0.15f;

        canvas.drawCircle(cx, cy, r, tlo);
        canvas.drawCircle(cx, cy, r, linie);
        canvas.drawCircle(cx, cy, rBabla * 1.3f, linie);
        canvas.drawLine(cx - r, cy, cx + r, cy, linie);
        canvas.drawLine(cx, cy - r, cx, cy + r, linie);

        // Bąbel ucieka w stronę "wyżej" położonej krawędzi (przeciwnie do grawitacji).
        float dx = -ax / MAX_G * (r - rBabla);
        float dy = ay / MAX_G * (r - rBabla);
        float dl = (float) Math.hypot(dx, dy);
        if (dl > r - rBabla) {
            dx = dx / dl * (r - rBabla);
            dy = dy / dl * (r - rBabla);
        }

        boolean poziom = Math.abs(ax) < 0.3f && Math.abs(ay) < 0.3f;
        babel.setColor(poziom ? Color.rgb(0, 160, 0) : Color.WHITE);
        canvas.drawCircle(cx + dx, cy + dy, rBabla, babel);
        canvas.drawCircle(cx + dx, cy + dy, rBabla, linie);

        double katX = Math.toDegrees(Math.asin(Math.max(-1, Math.min(1, ax / MAX_G))));
        double katY = Math.toDegrees(Math.asin(Math.max(-1, Math.min(1, ay / MAX_G))));
        canvas.drawText(String.format("X: %.1f°   Y: %.1f°", katX, katY),
                cx, cy + r + 80f, tekst);
        if (poziom) {
            canvas.drawText("POZIOM", cx, cy - r - 40f, tekst);
        }
    }
}
