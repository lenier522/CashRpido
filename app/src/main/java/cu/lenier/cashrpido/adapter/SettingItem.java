package cu.lenier.cashrpido.adapter;

public class SettingItem {
    private String title;
    private int iconResId;

    public SettingItem(String title, int iconResId) {
        this.title = title;
        this.iconResId = iconResId;
    }

    public String getTitle() {
        return title;
    }

    public int getIconResId() {
        return iconResId;
    }
}

