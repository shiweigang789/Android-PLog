package org.mym.plog;

import org.json.JSONObject;

/**
 * Builder style API; use this class to fit complicated needs.
 * <p>
 * NOTE: APIs in {@link PLog} class is for common scenarios and this class is for advanced usage.
 * Some decor method in {@link PLog} will NOT be added into this class, e.g.
 * {@link PLog#wtf(Throwable)}.
 * </p>
 *
 * @author Muyangmin
 * @since 2.0.0
 */
public final class LogRequest {

    @PrintLevel
    private int level;
    private int stackOffset;
    private String tag;
    private Category category;
    private String msg;
    private Object[] params;

    @PrintLevel
    public int getLevel() {
        return level;
    }

    public int getStackOffset() {
        return stackOffset;
    }

    public String getTag() {
        return tag;
    }

    public Category getCategory() {
        return category;
    }

    public String getMsg() {
        return msg;
    }

    public Object[] getParams() {
        return params;
    }

    // ----- NOT core parameters; just for better usage BEGIN -----
    public void json(JSONObject jsonObject) {
        params(jsonObject);
    }

    public void throwable(Throwable throwable) {
        params(throwable);
    }
    // ----- NOT core parameters; just for better usage END -----

    // -----BUILDER STYLE CODE BEGIN -----

    public LogRequest level(@PrintLevel int level) {
        this.level = level;
        return this;
    }

    public LogRequest tag(String tag) {
        this.tag = tag;
        return this;
    }

    public LogRequest category(Category category) {
        this.category = category;
        return this;
    }

    public LogRequest msg(String msg) {
        this.msg = msg;
        return this;
    }

    public LogRequest params(Object... params) {
        this.params = params;
        return this;
    }

    public LogRequest stackOffset(int stackOffset) {
        this.stackOffset = stackOffset;
        return this;
    }
    // -----BUILDER STYLE CODE END -----

    public void execute() {
        LogEngine.handleLogRequest(this);
    }
}
