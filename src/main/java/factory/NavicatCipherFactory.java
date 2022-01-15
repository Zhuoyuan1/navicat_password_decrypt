package factory;

import enums.VersionEnum;
import lombok.SneakyThrows;
import navicat.Navicat11Cipher;
import navicat.Navicat12Cipher;
import navicat.NavicatChiper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * NavicatCipherFactory
 *
 * @author lzy
 * @date 2021/01/14 15:58
 **/
public class NavicatCipherFactory {
    /**
     * NavicatCipher缓存池
     */
    private static final Map<String, NavicatChiper> REPORT_POOL = new ConcurrentHashMap<>(0);

    static {
        REPORT_POOL.put(VersionEnum.native11.name(), new Navicat11Cipher());
        REPORT_POOL.put(VersionEnum.navicat12more.name(), new Navicat12Cipher());
    }

    /**
     * 获取对应数据源
     *
     * @param type 类型
     * @return ITokenGranter
     */
    @SneakyThrows
    public static NavicatChiper get(String type) {
        NavicatChiper chiper = REPORT_POOL.get(type);
        if (chiper == null) {
            throw new ClassNotFoundException("no NavicatCipher was found");
        } else {
            return chiper;
        }
    }
}
