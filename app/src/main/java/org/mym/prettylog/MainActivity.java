package org.mym.prettylog;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.mym.plog.DebugPrinter;
import org.mym.plog.PLog;
import org.mym.plog.printer.FilePrinter;
import org.mym.plog.timing.TimingLogger;
import org.mym.prettylog.data.JSONEntity;
import org.mym.prettylog.data.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final int LOG_BASIC = 758;
    private static final int LOG_LONG = LOG_BASIC + 1;
    private static final int LOG_TO_FILE = LOG_LONG + 1;
    private static final int LOG_JSON = LOG_TO_FILE + 1;
    private static final int LOG_THROWABLE = LOG_JSON + 1;
    private static final int LOG_POJO = LOG_THROWABLE + 1;
    private static final int LOG_TIMING = LOG_POJO + 1;
    @BindView(R.id.main_recycler_usage)
    RecyclerView mRecyclerView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //TODO add action button
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setAdapter(new UsageAdapter(new int[]{
                LOG_BASIC, LOG_LONG, LOG_TO_FILE,
                LOG_JSON, LOG_POJO, LOG_THROWABLE,
                LOG_TIMING
        }, getResources().getStringArray(R.array.usage_cases)));
    }

    private void performClick(@UsageCase int action) {
        switch (action) {
            case LOG_BASIC:
                logBasic();
                break;
            case LOG_JSON:
                logJSON();
                break;
            case LOG_LONG:
                logLong();
                break;
            case LOG_POJO:
                logObjects();
                break;
            case LOG_THROWABLE:
                logThrowable();
                break;
            case LOG_TIMING:
                logTiming();
                break;
            case LOG_TO_FILE:
                logToFile();
                break;
        }
    }

    void logBasic() {
        PLog.empty();

        PLog.v("This is a verbose log.");
        PLog.d("This is a debug log. param is %d, %.2f and %s", 1, 2.413221, "Great");
        PLog.i("InfoTag", "This is an info log using specified tag.");
        PLog.w("This is a warn log.");
        PLog.e("This is an error log.");
    }

    void logLong() {
        PLog.i("Here is one line of text that is going to be wrapped after 20 columns.Here is one" +
                " line of text that is going to be wrapped after 20 columns.Here is one line of " +
                "text that is going to be wrapped after 20 columns.");

        StringBuilder sb = new StringBuilder("无限长字符串测试");
        for (int i = 0; i < 100; i++) {
            sb.append("[").append(i).append("]");
        }
        PLog.d(sb.toString());
    }

    void logObjects() {
        /**
         * User class is a data class without toString() method overridden.
         */
        User.Cat tom = new User.Cat("Tom", "Blue");
        User.Cat jerry = new User.Cat("Jerry", "brown");

        List<User.Cat> cats = new ArrayList<>();
        Collections.addAll(cats, tom, jerry);

        User user = new User("Alice", 23, cats);

        PLog.objects(cats);
        PLog.objects(user);

        //Log multi objects.
        PLog.objects("RxJava", "RxAndroid", "RxBinding", "RxBus");

    }

    void logThrowable() {
        NullPointerException e = new NullPointerException("This is a sample exception!");
        //PLog can log exceptions in all levels, WARN and ERROR is recommended.
        PLog.throwable(e);

        //force using error level
        PLog.level(Log.ERROR).throwable(e);
    }

    void logJSON() {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(JSONEntity.DATA);
            PLog.json(jsonObject);
        } catch (JSONException e) {
            PLog.throwable(e);
        }
    }

    /**
     * To use built-in {@link FilePrinter} class, plog-printer dependency is required.
     * See wiki for more details.
     */
    void logToFile() {
        FilePrinter printer = new FilePrinter(this);
        //ONLY printers changed
        PLog.prepare(new DebugPrinter(BuildConfig.DEBUG), printer);
        PLog.i("This is the first line of file log.");

        PLog.v("file length can be very long");

        PLog.i("This is the last line of file log.");

        Toast.makeText(this, getString(R.string.file_logger_tip, printer.getLogFilePath()), Toast
                .LENGTH_SHORT).show();

        //RESET printers
        printer.close();
        PLog.prepare(new DebugPrinter(BuildConfig.DEBUG));
    }

    /**
     * To use built-in {@link TimingLogger} class, plog-timing dependency is required.
     * See wiki for more details.
     * <p>
     * The usage is totally same as {@link android.util.TimingLogger}; all you should remember is
     * just using right class reference.
     * </p>
     */
    void logTiming() {
        TimingLogger logger = new TimingLogger("TimingTag", "OperationName");

        logger.addSplit("Operation Step1");
        emulateTimeOperation();

        logger.addSplit("Operation Step2");
        emulateTimeOperation();

        logger.addSplit("Operation FINISHED");

        logger.dumpToLog();
    }

    private void emulateTimeOperation() {
        Random random = new Random();
        try {
            Thread.sleep(random.nextInt(200));
        } catch (InterruptedException ignored) {

        }
    }

    @IntDef({LOG_BASIC, LOG_LONG, LOG_TO_FILE,
            LOG_JSON, LOG_POJO, LOG_THROWABLE,
            LOG_TIMING})
    private @interface UsageCase {

    }

    private class UsageAdapter extends RecyclerView.Adapter<UsageHolder> {

        private String[] mTexts;
        @UsageCase
        private int[] mActions;

        private int mItemCount;

        public UsageAdapter(int[] actions, String[] texts) {
            if (actions == null || texts == null || actions.length != texts.length) {
                throw new IllegalArgumentException("Wrong array!");
            }
            this.mActions = actions;
            this.mTexts = texts;
            mItemCount = actions.length;
        }

        @Override
        public UsageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_usage_case, parent, false);
            return new UsageHolder(view);
        }

        @Override
        public void onBindViewHolder(final UsageHolder holder, final int position) {
            holder.displayData(mTexts[position]);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    performClick(mActions[holder.getAdapterPosition()]);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mItemCount;
        }
    }

    private class UsageHolder extends RecyclerView.ViewHolder {

        private TextView tvContent;

        public UsageHolder(View itemView) {
            super(itemView);
            tvContent = ButterKnife.findById(itemView, R.id.item_main_content);
        }

        public void displayData(String text) {
            tvContent.setText(text);
        }
    }
}
