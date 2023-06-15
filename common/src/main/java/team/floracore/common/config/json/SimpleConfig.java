package team.floracore.common.config.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;

/**
 * @author xLikeWATCHDOG
 */
public class SimpleConfig<T> implements IConfig<T> {
    public File conf;
    public T con = null;
    public Gson g = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();
    public String encode;
    public Class<T> clazz;
    public Constructor<T> cons;

    public SimpleConfig(String loc, String encode, Class<T> clazz) {
        this.conf = new File(loc);
        this.encode = encode;
        this.clazz = clazz;
        try {
            this.cons = this.clazz.getDeclaredConstructor();
            this.cons.setAccessible(true);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void autoCreateNewFile() throws IOException {
        if (!this.conf.isFile()) {
            this.conf.getParentFile().mkdirs();
            this.conf.createNewFile();
            initConfig();
        }
    }

    /**
     * 初始化JSON
     *
     * @throws IOException 异常
     */
    public void initConfig() throws IOException {
        try {
            this.con = getDefaultConfig();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        this.saveConfig(false);
    }

    /**
     * 加载JSON
     *
     * @throws IOException 异常
     */
    @Override
    public void loadConfig() throws IOException {
        autoCreateNewFile();
        try (FileInputStream i = new FileInputStream(conf)) {
            byte[] buf = new byte[i.available()];
            i.read(buf);
            this.con = this.g.fromJson(new String(buf, this.encode), this.clazz);
            if (this.con == null) {
                initConfig();
            }
        }
    }

    /**
     * 获得默认的JSON
     *
     * @return 泛型
     */
    public T getDefaultConfig() {
        try {
            return this.cons.newInstance();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 保存JSON
     *
     * @throws IOException 异常
     */
    @Override
    public void saveConfig() throws IOException {
        this.saveConfig(true);
    }

    /**
     * 保存JSON
     *
     * @param ac 参数1，可不选，默认true
     * @throws IOException 异常
     */
    private void saveConfig(boolean ac) throws IOException {
        if (ac) {
            autoCreateNewFile();
        }
        try (FileOutputStream i = new FileOutputStream(conf)) {
            i.write(this.g.toJson(this.con, this.clazz).getBytes(this.encode));
        }
    }

    @Override
    public T getConfig() {
        return con;
    }
}