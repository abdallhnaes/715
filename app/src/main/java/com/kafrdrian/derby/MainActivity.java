package com.kafrdrian.derby;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
    private final int BG = Color.rgb(5, 14, 11);
    private final int PANEL = Color.rgb(8, 28, 31);
    private final int PANEL_2 = Color.rgb(10, 42, 42);
    private final int GOLD = Color.rgb(216, 180, 90);

    private SharedPreferences sp;
    private LinearLayout root;

    private Team teamA = new Team("كفردريان",
            new String[]{"ابو الليث","حسونة","عبدالله","عبدالخالق","سلوم","جليبيب","ابو احمد","يوسف"},
            "مصطفى عثمان", "3-3-1");
    private Team teamB = new Team("الفريق الثاني",
            new String[]{"الحارس","مدافع 1","مدافع 2","مدافع 3","وسط 1","محور","وسط 2","مهاجم"},
            "بديل", "3-3-1");

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        sp = getSharedPreferences("derby_kafrdrian_v5", MODE_PRIVATE);
        load();
        showHome();
    }

    private void load() {
        loadTeam(teamA, "a");
        loadTeam(teamB, "b");
    }

    private void loadTeam(Team t, String k) {
        t.name = sp.getString(k + "_name", t.name);
        t.formation = sp.getString(k + "_form", t.formation);
        t.substitute = sp.getString(k + "_sub", t.substitute);
        for (int i=0;i<8;i++) {
            t.players[i] = sp.getString(k + "_p" + i, t.players[i]);
            t.x[i] = sp.getFloat(k + "_x" + i, defaultX(i));
            t.y[i] = sp.getFloat(k + "_y" + i, defaultY(i));
        }
    }

    private void save() {
        saveTeam(teamA, "a");
        saveTeam(teamB, "b");
    }

    private void saveTeam(Team t, String k) {
        SharedPreferences.Editor e = sp.edit();
        e.putString(k + "_name", t.name);
        e.putString(k + "_form", t.formation);
        e.putString(k + "_sub", t.substitute);
        for (int i=0;i<8;i++) {
            e.putString(k + "_p" + i, t.players[i]);
            e.putFloat(k + "_x" + i, t.x[i]);
            e.putFloat(k + "_y" + i, t.y[i]);
        }
        e.apply();
    }

    private float defaultX(int i) {
        float[] xs = {.50f,.22f,.50f,.78f,.30f,.50f,.70f,.50f};
        return xs[i];
    }

    private float defaultY(int i) {
        float[] ys = {.91f,.74f,.74f,.74f,.50f,.58f,.50f,.28f};
        return ys[i];
    }

    private void base() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);
        root.setPadding(dp(8), dp(8), dp(8), dp(8));
        setContentView(root);
    }

    private TextView text(String s, int size, int color, int style) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setTypeface(Typeface.DEFAULT_BOLD, style);
        t.setGravity(Gravity.CENTER);
        t.setPadding(dp(8), dp(6), dp(8), dp(6));
        return t;
    }

    private TextView label(String s) {
        TextView l = text(s, 14, GOLD, Typeface.BOLD);
        l.setGravity(Gravity.RIGHT);
        return l;
    }

    private Button button(String s) {
        Button b = new Button(this);
        b.setText(s);
        b.setTextSize(14);
        b.setTextColor(Color.WHITE);
        b.setAllCaps(false);
        b.setBackground(round(PANEL_2, dp(14), GOLD, 1));
        return b;
    }

    private GradientDrawable round(int color, int radius, int strokeColor, int stroke) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.setCornerRadius(radius);
        if (stroke > 0) gd.setStroke(dp(stroke), strokeColor);
        return gd;
    }

    private EditText input(String value) {
        EditText e = new EditText(this);
        e.setText(value);
        e.setTextSize(16);
        e.setTextColor(Color.WHITE);
        e.setSingleLine(true);
        e.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        e.setPadding(dp(12), 0, dp(12), 0);
        e.setInputType(InputType.TYPE_CLASS_TEXT);
        e.setBackground(round(Color.rgb(12, 38, 40), dp(12), Color.rgb(38, 76, 76), 1));
        return e;
    }

    private void header(String active) {
        LinearLayout top = new LinearLayout(this);
        top.setOrientation(LinearLayout.VERTICAL);
        top.setBackground(round(Color.rgb(3, 23, 20), dp(18), GOLD, 1));
        top.setPadding(dp(6), dp(6), dp(6), dp(6));

        top.addView(text("ديربي كفردريان", 25, Color.WHITE, Typeface.BOLD));
        top.addView(text("خطط الفريقين ونتائج الديربي", 13, Color.LTGRAY, Typeface.BOLD));

        int[] stats = getStats();
        LinearLayout score = new LinearLayout(this);
        score.setOrientation(LinearLayout.HORIZONTAL);
        score.setGravity(Gravity.CENTER);
        score.setPadding(dp(4), dp(6), dp(4), dp(6));
        score.addView(scoreCard(teamA.name, String.valueOf(stats[0]), "فوز"), new LinearLayout.LayoutParams(0, dp(86), 1));
        score.addView(scoreCard("تعادل", String.valueOf(stats[1]), "مباراة"), new LinearLayout.LayoutParams(0, dp(86), 1));
        score.addView(scoreCard(teamB.name, String.valueOf(stats[2]), "فوز"), new LinearLayout.LayoutParams(0, dp(86), 1));
        top.addView(score);
        root.addView(top, new LinearLayout.LayoutParams(-1, -2));

        LinearLayout tabs = new LinearLayout(this);
        tabs.setOrientation(LinearLayout.HORIZONTAL);
        String[] tabsText = {"الرئيسية","الفريق 1","الفريق 2","النتيجة","السجل"};
        for (String s: tabsText) {
            Button b = button(s.equals(active) ? "● " + s : s);
            tabs.addView(b, new LinearLayout.LayoutParams(0, dp(48), 1));
            if (s.equals("الرئيسية")) b.setOnClickListener(v -> showHome());
            if (s.equals("الفريق 1")) b.setOnClickListener(v -> showTeamEditor(true));
            if (s.equals("الفريق 2")) b.setOnClickListener(v -> showTeamEditor(false));
            if (s.equals("النتيجة")) b.setOnClickListener(v -> showAddResult());
            if (s.equals("السجل")) b.setOnClickListener(v -> showHistory());
        }
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, dp(6), 0, dp(6));
        root.addView(tabs, lp);
    }

    private View scoreCard(String title, String num, String label) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);
        card.setBackground(round(Color.rgb(8, 31, 33), dp(14), Color.rgb(75, 63, 31), 1));
        card.addView(text(title, 13, Color.WHITE, Typeface.BOLD));
        card.addView(text(num, 24, GOLD, Typeface.BOLD));
        card.addView(text(label, 11, Color.LTGRAY, Typeface.BOLD));
        return card;
    }

    private int[] getStats() {
        String history = sp.getString("history", "").trim();
        int a=0,d=0,b=0;
        if (!history.isEmpty()) {
            String[] rows = history.split("\\n");
            for (String r: rows) {
                try {
                    String[] parts = r.split("\\|");
                    int ga = Integer.parseInt(parts[2].trim());
                    int gb = Integer.parseInt(parts[3].trim());
                    if (ga > gb) a++;
                    else if (gb > ga) b++;
                    else d++;
                } catch(Exception ignored) {}
            }
        }
        return new int[]{a,d,b};
    }

    private void showHome() {
        load();
        base();
        header("الرئيسية");

        ScrollView sv = new ScrollView(this);
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        sv.addView(box);

        box.addView(sectionTitle("خطة الفريقين"));
        TacticsView viewA = new TacticsView(this);
        viewA.setTeam(teamA, false, false);
        box.addView(sectionTitle(teamA.name + " | " + teamA.formation));
        box.addView(viewA, new LinearLayout.LayoutParams(-1, dp(760)));

        TacticsView viewB = new TacticsView(this);
        viewB.setTeam(teamB, false, false);
        box.addView(sectionTitle(teamB.name + " | " + teamB.formation));
        box.addView(viewB, new LinearLayout.LayoutParams(-1, dp(760)));

        root.addView(sv, new LinearLayout.LayoutParams(-1, 0, 1));
    }

    private TextView sectionTitle(String s) {
        TextView t = text(s, 17, GOLD, Typeface.BOLD);
        t.setBackground(round(Color.rgb(7, 25, 28), dp(12), GOLD, 1));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, dp(8), 0, dp(5));
        t.setLayoutParams(lp);
        return t;
    }

    private void showTeamEditor(boolean first) {
        load();
        base();
        header(first ? "الفريق 1" : "الفريق 2");

        Team team = first ? teamA : teamB;
        ScrollView sv = new ScrollView(this);
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(4), dp(4), dp(4), dp(24));
        sv.addView(box);

        box.addView(sectionTitle(first ? "تعديل الفريق الأول" : "تعديل الفريق الثاني"));

        box.addView(label("اسم الفريق"));
        EditText teamName = input(team.name);
        box.addView(teamName, new LinearLayout.LayoutParams(-1, dp(52)));

        box.addView(label("الخطة"));
        Spinner spinner = new Spinner(this);
        String[] forms = {"3-3-1", "3-2-2", "2-3-2", "1-3-3", "خطة يدوية"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, forms);
        spinner.setAdapter(adapter);
        int idx = 0;
        for (int i=0;i<forms.length;i++) if (forms[i].equals(team.formation)) idx=i;
        spinner.setSelection(idx);
        box.addView(spinner, new LinearLayout.LayoutParams(-1, dp(52)));

        String[] labels = {"الحارس","لاعب 2","لاعب 3","لاعب 4","لاعب 5","لاعب 6","لاعب 7","لاعب 8"};
        EditText[] inputs = new EditText[8];
        for (int i=0;i<8;i++) {
            box.addView(label(labels[i]));
            inputs[i] = input(team.players[i]);
            box.addView(inputs[i], new LinearLayout.LayoutParams(-1, dp(52)));
        }

        box.addView(label("البديل"));
        EditText sub = input(team.substitute);
        box.addView(sub, new LinearLayout.LayoutParams(-1, dp(52)));

        Button save = button("حفظ أسماء وخطة الفريق");
        save.setOnClickListener(v -> {
            team.name = clean(teamName.getText().toString(), first ? "الفريق الأول" : "الفريق الثاني");
            String oldFormation = team.formation;
            team.formation = spinner.getSelectedItem().toString();
            for (int i=0;i<8;i++) team.players[i] = clean(inputs[i].getText().toString(), labels[i]);
            team.substitute = clean(sub.getText().toString(), "بديل");
            if (!team.formation.equals("خطة يدوية") && !team.formation.equals(oldFormation)) {
                applyFormationDefaults(team);
            }
            save();
            hideKeyboard();
            Toast.makeText(this, "تم حفظ الفريق", Toast.LENGTH_SHORT).show();
            showHome();
        });
        box.addView(save, new LinearLayout.LayoutParams(-1, dp(58)));

        Button custom = button("رسم الخطة على كيفي بالسحب");
        custom.setOnClickListener(v -> {
            team.name = clean(teamName.getText().toString(), first ? "الفريق الأول" : "الفريق الثاني");
            team.formation = "خطة يدوية";
            for (int i=0;i<8;i++) team.players[i] = clean(inputs[i].getText().toString(), labels[i]);
            team.substitute = clean(sub.getText().toString(), "بديل");
            save();
            hideKeyboard();
            showManualPlan(first);
        });
        box.addView(custom, new LinearLayout.LayoutParams(-1, dp(58)));

        TextView hint = text("في الخطة اليدوية: اضغط زر الرسم ثم اسحب كل لاعب لأي مكان تريده داخل الملعب.", 14, Color.LTGRAY, Typeface.BOLD);
        hint.setGravity(Gravity.RIGHT);
        box.addView(hint);

        root.addView(sv, new LinearLayout.LayoutParams(-1, 0, 1));
    }

    private void applyFormationDefaults(Team team) {
        float[][] arr = TacticsView.getFormation(team.formation);
        for (int i=0;i<8;i++) {
            team.x[i] = arr[i][0];
            team.y[i] = arr[i][1];
        }
    }

    private void showManualPlan(boolean first) {
        load();
        base();
        header(first ? "الفريق 1" : "الفريق 2");
        Team team = first ? teamA : teamB;

        TextView hint = text("اسحب اللاعبين بأصبعك وضعهم كما تريد، ثم اضغط حفظ.", 15, Color.WHITE, Typeface.BOLD);
        root.addView(hint);

        ManualTacticsView editor = new ManualTacticsView(this);
        editor.setTeam(team);
        root.addView(editor, new LinearLayout.LayoutParams(-1, 0, 1));

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);

        Button reset = button("ترتيب 3-3-1");
        reset.setOnClickListener(v -> {
            float[][] arr = TacticsView.getFormation("3-3-1");
            for (int i=0;i<8;i++) {
                team.x[i] = arr[i][0];
                team.y[i] = arr[i][1];
            }
            editor.invalidate();
        });

        Button saveBtn = button("حفظ الخطة اليدوية");
        saveBtn.setOnClickListener(v -> {
            team.formation = "خطة يدوية";
            save();
            Toast.makeText(this, "تم حفظ الخطة اليدوية", Toast.LENGTH_SHORT).show();
            showHome();
        });

        actions.addView(reset, new LinearLayout.LayoutParams(0, dp(56), 1));
        actions.addView(saveBtn, new LinearLayout.LayoutParams(0, dp(56), 1));
        root.addView(actions);
    }

    private void showAddResult() {
        load();
        base();
        header("النتيجة");

        ScrollView sv = new ScrollView(this);
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(4), dp(4), dp(4), dp(24));
        sv.addView(box);

        box.addView(sectionTitle("تسجيل نتيجة مباراة جديدة"));
        box.addView(text(teamA.name + "  ×  " + teamB.name, 22, Color.WHITE, Typeface.BOLD));

        EditText date = input(new SimpleDateFormat("yyyy/MM/dd", Locale.US).format(new Date()));
        EditText goalsA = input("0");
        EditText goalsB = input("0");
        goalsA.setInputType(InputType.TYPE_CLASS_NUMBER);
        goalsB.setInputType(InputType.TYPE_CLASS_NUMBER);

        box.addView(label("تاريخ المباراة"));
        box.addView(date, new LinearLayout.LayoutParams(-1, dp(52)));
        box.addView(label("أهداف " + teamA.name));
        box.addView(goalsA, new LinearLayout.LayoutParams(-1, dp(52)));
        box.addView(label("أهداف " + teamB.name));
        box.addView(goalsB, new LinearLayout.LayoutParams(-1, dp(52)));

        box.addView(sectionTitle("خطة " + teamA.name));
        TacticsView v1 = new TacticsView(this);
        v1.setTeam(teamA, false, false);
        box.addView(v1, new LinearLayout.LayoutParams(-1, dp(620)));

        box.addView(sectionTitle("خطة " + teamB.name));
        TacticsView v2 = new TacticsView(this);
        v2.setTeam(teamB, false, false);
        box.addView(v2, new LinearLayout.LayoutParams(-1, dp(620)));

        Button saveResult = button("حفظ النتيجة وتحديث الانتصارات");
        saveResult.setOnClickListener(v -> {
            int a = parse(goalsA.getText().toString());
            int b = parse(goalsB.getText().toString());
            String record = date.getText().toString().trim() + "|" + teamA.name + " ضد " + teamB.name + "|" + a + "|" + b +
                    "|" + teamA.formation + "|" + teamB.formation;
            String old = sp.getString("history", "");
            sp.edit().putString("history", record + "\n" + old).apply();
            hideKeyboard();
            Toast.makeText(this, "تم حفظ النتيجة", Toast.LENGTH_SHORT).show();
            showHistory();
        });
        box.addView(saveResult, new LinearLayout.LayoutParams(-1, dp(60)));

        root.addView(sv, new LinearLayout.LayoutParams(-1, 0, 1));
    }

    private void showHistory() {
        load();
        base();
        header("السجل");

        ScrollView sv = new ScrollView(this);
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(4), dp(4), dp(4), dp(24));
        sv.addView(box);

        box.addView(sectionTitle("سجل مباريات الديربي"));

        String history = sp.getString("history", "").trim();
        if (history.isEmpty()) {
            box.addView(text("لا توجد مباريات مسجلة بعد", 17, Color.LTGRAY, Typeface.BOLD));
        } else {
            String[] rows = history.split("\\n");
            for (String r: rows) addHistoryCard(box, r);
        }

        Button clear = button("مسح السجل كاملاً");
        clear.setOnClickListener(v -> {
            sp.edit().remove("history").apply();
            showHistory();
        });
        box.addView(clear, new LinearLayout.LayoutParams(-1, dp(58)));

        root.addView(sv, new LinearLayout.LayoutParams(-1, 0, 1));
    }

    private void addHistoryCard(LinearLayout box, String r) {
        String[] parts = r.split("\\|");
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackground(round(PANEL, dp(14), GOLD, 1));
        card.setPadding(dp(8), dp(8), dp(8), dp(8));

        String date = parts.length>0 ? parts[0] : "";
        String match = parts.length>1 ? parts[1] : "";
        String a = parts.length>2 ? parts[2] : "0";
        String b = parts.length>3 ? parts[3] : "0";
        String fa = parts.length>4 ? parts[4] : "";
        String fb = parts.length>5 ? parts[5] : "";

        card.addView(text(date, 13, Color.LTGRAY, Typeface.BOLD));
        card.addView(text(match, 18, Color.WHITE, Typeface.BOLD));
        card.addView(text(a + "  -  " + b, 28, GOLD, Typeface.BOLD));
        card.addView(text("الخطة: " + fa + " / " + fb, 12, Color.LTGRAY, Typeface.BOLD));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, dp(6), 0, dp(6));
        box.addView(card, lp);
    }

    private String clean(String s, String fallback) {
        s = s.trim();
        return s.isEmpty() ? fallback : s;
    }

    private int parse(String s) {
        try { return Integer.parseInt(s.trim()); } catch(Exception e) { return 0; }
    }

    private void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            View view = getCurrentFocus();
            if (imm != null && view != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception ignored) {}
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density + 0.5f);
    }

    static class Team {
        String name;
        String[] players;
        String substitute;
        String formation;
        float[] x = new float[8];
        float[] y = new float[8];

        Team(String name, String[] players, String substitute, String formation) {
            this.name = name;
            this.players = players;
            this.substitute = substitute;
            this.formation = formation;
            float[][] arr = TacticsView.getFormation(formation);
            for (int i=0;i<8;i++) {
                x[i] = arr[i][0];
                y[i] = arr[i][1];
            }
        }
    }

    public static class ManualTacticsView extends TacticsView {
        private int active = -1;

        public ManualTacticsView(Context c) {
            super(c);
        }

        public void setTeam(Team t) {
            super.setTeam(t, false, false);
        }

        @Override
        public boolean onTouchEvent(android.view.MotionEvent e) {
            if (team == null) return true;
            RectF r = currentPitch();
            if (r == null) return true;

            float mx = e.getX();
            float my = e.getY();

            if (e.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                active = nearest(mx, my, r);
                return true;
            }
            if (e.getAction() == android.view.MotionEvent.ACTION_MOVE && active >= 0) {
                float nx = (mx - r.left) / r.width();
                float ny = (my - r.top) / r.height();
                if (nx < .06f) nx = .06f;
                if (nx > .94f) nx = .94f;
                if (ny < .06f) ny = .06f;
                if (ny > .94f) ny = .94f;
                team.x[active] = nx;
                team.y[active] = ny;
                invalidate();
                return true;
            }
            if (e.getAction() == android.view.MotionEvent.ACTION_UP || e.getAction() == android.view.MotionEvent.ACTION_CANCEL) {
                active = -1;
                return true;
            }
            return true;
        }

        private int nearest(float mx, float my, RectF r) {
            int best = 0;
            double dist = 999999;
            for (int i=0;i<8;i++) {
                float px = r.left + r.width() * team.x[i];
                float py = r.top + r.height() * team.y[i];
                double d = Math.hypot(mx-px, my-py);
                if (d < dist) { dist = d; best = i; }
            }
            return best;
        }
    }

    public static class TacticsView extends View {
        protected Team team;
        protected boolean arrows;
        protected boolean footer;
        protected Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        private RectF lastPitch;

        public TacticsView(Context c) {
            super(c);
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        public void setTeam(Team t, boolean arrows, boolean footer) {
            this.team = t;
            this.arrows = arrows;
            this.footer = footer;
        }

        protected RectF currentPitch() {
            return lastPitch;
        }

        @Override
        protected void onDraw(Canvas c) {
            if (team == null) return;
            drawPoster(c, getWidth(), getHeight(), team, arrows, footer);
        }

        protected void drawPoster(Canvas c, int w, int h, Team team, boolean arrows, boolean footer) {
            int gold = Color.rgb(216,180,90);
            p.setStyle(Paint.Style.FILL);
            p.setColor(Color.rgb(6,16,12));
            c.drawRect(0,0,w,h,p);

            RectF pitch = new RectF(w*.07f, h*.08f, w*.93f, h*(footer?.82f:.96f));
            lastPitch = pitch;
            drawStadium(c, pitch);
            drawPitch(c, pitch);

            p.setTextAlign(Paint.Align.CENTER);
            p.setTypeface(Typeface.DEFAULT_BOLD);
            p.setTextSize(h*.035f);
            p.setColor(Color.WHITE);
            c.drawText(team.name + " | " + team.formation, w/2f, h*.055f, p);

            float[][] ps = positions(pitch, team);
            if (arrows) drawArrows(c, ps);

            int[] nums = {1,2,3,4,5,6,7,8};
            for (int i=0;i<8;i++) drawPlayer(c, ps[i][0], ps[i][1], nums[i], team.players[i]);

            if (footer) {
                RectF f = new RectF(w*.10f, h*.845f, w*.90f, h*.955f);
                p.setColor(Color.rgb(6,28,32));
                p.setStyle(Paint.Style.FILL);
                c.drawRoundRect(f, 16, 16, p);
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(2);
                p.setColor(gold);
                c.drawRoundRect(f, 16, 16, p);
                p.setStyle(Paint.Style.FILL);
                p.setColor(Color.WHITE);
                p.setTextSize(h*.025f);
                c.drawText("البديل: " + team.substitute, f.centerX(), f.centerY()+8, p);
            }
        }

        private void drawStadium(Canvas c, RectF pitch) {
            RectF outer = new RectF(pitch.left-36, pitch.top-36, pitch.right+36, pitch.bottom+24);
            p.setStyle(Paint.Style.FILL);
            p.setColor(Color.rgb(226,221,210));
            c.drawRoundRect(outer, 12, 12, p);

            p.setColor(Color.rgb(120,26,24));
            c.drawRoundRect(new RectF(pitch.left-10,pitch.top-10,pitch.right+10,pitch.bottom+10),6,6,p);

            RectF stand = new RectF(pitch.left, outer.top+8, pitch.right, pitch.top-14);
            p.setColor(Color.rgb(42,26,36));
            c.drawRoundRect(stand,6,6,p);
            p.setColor(Color.rgb(150,92,112));
            for(int i=1;i<5;i++) c.drawRect(stand.left, stand.top+i*stand.height()/5f, stand.right, stand.top+i*stand.height()/5f+3, p);

            p.setColor(Color.argb(45,80,80,80));
            p.setStrokeWidth(1);
            for(float x=outer.left;x<outer.right;x+=22) c.drawLine(x,outer.top,x,outer.bottom,p);
            for(float y=outer.top;y<outer.bottom;y+=22) c.drawLine(outer.left,y,outer.right,y,p);
            p.setStyle(Paint.Style.FILL);
        }

        private void drawPitch(Canvas c, RectF r) {
            p.setStyle(Paint.Style.FILL);
            int cols=12, rows=12;
            for(int i=0;i<cols;i++) {
                p.setColor(i%2==0?Color.rgb(25,140,30):Color.rgb(18,112,26));
                c.drawRect(r.left+i*r.width()/cols, r.top, r.left+(i+1)*r.width()/cols, r.bottom,p);
            }
            for(int i=0;i<cols;i++){
                for(int j=0;j<rows;j++){
                    p.setColor((i+j)%2==0?Color.argb(22,75,185,55):Color.argb(22,0,55,0));
                    c.drawRect(r.left+i*r.width()/cols, r.top+j*r.height()/rows,
                            r.left+(i+1)*r.width()/cols, r.top+(j+1)*r.height()/rows,p);
                }
            }

            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(Math.max(3, r.width()*.004f));
            p.setColor(Color.WHITE);
            c.drawRect(r,p);
            c.drawLine(r.left,r.centerY(),r.right,r.centerY(),p);
            c.drawCircle(r.centerX(),r.centerY(),r.width()*.12f,p);
            float boxW=r.width()*.36f, boxH=r.height()*.12f;
            c.drawRect(r.centerX()-boxW/2,r.top,r.centerX()+boxW/2,r.top+boxH,p);
            c.drawRect(r.centerX()-boxW/2,r.bottom-boxH,r.centerX()+boxW/2,r.bottom,p);
            float smallW=r.width()*.18f, smallH=r.height()*.055f;
            c.drawRect(r.centerX()-smallW/2,r.top,r.centerX()+smallW/2,r.top+smallH,p);
            c.drawRect(r.centerX()-smallW/2,r.bottom-smallH,r.centerX()+smallW/2,r.bottom,p);
            c.drawRect(r.centerX()-r.width()*.055f,r.top-11,r.centerX()+r.width()*.055f,r.top,p);
            c.drawRect(r.centerX()-r.width()*.055f,r.bottom,r.centerX()+r.width()*.055f,r.bottom+11,p);
            p.setStyle(Paint.Style.FILL);
        }

        protected float[][] positions(RectF r, Team team) {
            if ("خطة يدوية".equals(team.formation)) {
                float[][] out = new float[8][2];
                for (int i=0;i<8;i++) {
                    out[i][0] = r.left + r.width()*team.x[i];
                    out[i][1] = r.top + r.height()*team.y[i];
                }
                return out;
            }
            float[][] f = getFormation(team.formation);
            float[][] out = new float[8][2];
            for (int i=0;i<8;i++) {
                out[i][0] = r.left + r.width()*f[i][0];
                out[i][1] = r.top + r.height()*f[i][1];
            }
            return out;
        }

        public static float[][] getFormation(String formation) {
            if ("3-2-2".equals(formation)) {
                return new float[][]{{.50f,.91f},{.22f,.74f},{.50f,.74f},{.78f,.74f},{.35f,.54f},{.65f,.54f},{.38f,.30f},{.62f,.30f}};
            } else if ("2-3-2".equals(formation)) {
                return new float[][]{{.50f,.91f},{.32f,.74f},{.68f,.74f},{.26f,.55f},{.50f,.55f},{.74f,.55f},{.38f,.30f},{.62f,.30f}};
            } else if ("1-3-3".equals(formation)) {
                return new float[][]{{.50f,.91f},{.50f,.74f},{.25f,.54f},{.50f,.54f},{.75f,.54f},{.27f,.30f},{.50f,.30f},{.73f,.30f}};
            }
            return new float[][]{{.50f,.91f},{.22f,.74f},{.50f,.74f},{.78f,.74f},{.30f,.50f},{.50f,.58f},{.70f,.50f},{.50f,.28f}};
        }

        private void drawPlayer(Canvas c,float x,float y,int num,String name){
            float rad=24;
            p.setShader(new RadialGradient(x-rad/3,y-rad/3,rad*1.5f,Color.rgb(10,90,210),Color.rgb(0,28,105), Shader.TileMode.CLAMP));
            p.setStyle(Paint.Style.FILL);
            c.drawCircle(x,y,rad,p);
            p.setShader(null);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(3);
            p.setColor(Color.WHITE);
            c.drawCircle(x,y,rad,p);
            p.setStyle(Paint.Style.FILL);
            p.setTextAlign(Paint.Align.CENTER);
            p.setTypeface(Typeface.DEFAULT_BOLD);
            p.setColor(Color.WHITE);
            p.setTextSize(22);
            c.drawText(String.valueOf(num),x,y+8,p);

            RectF card=new RectF(x-60,y+29,x+60,y+61);
            p.setColor(Color.rgb(7,24,30));
            c.drawRoundRect(card,8,8,p);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(2);
            p.setColor(Color.rgb(216,180,90));
            c.drawRoundRect(card,8,8,p);
            p.setStyle(Paint.Style.FILL);
            p.setColor(Color.WHITE);
            p.setTextSize(17);
            c.drawText(name,x,y+52,p);
        }

        private void drawArrows(Canvas c, float[][] ps) {}
    }
}
