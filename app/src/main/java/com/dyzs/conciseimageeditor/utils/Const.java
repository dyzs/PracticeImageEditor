package com.dyzs.conciseimageeditor.utils;

import android.os.Environment;

/**
 * Created by maidou on 2016/1/13.
 */
public interface Const {

    // sd 卡根目录
    String SDCARD_ROOT = Environment.getExternalStorageDirectory().getPath();

    // 文件根目录前缀
    String FILE_PREFIX = "file://";

    // 文件的父目录
    String STICKER = FILE_PREFIX + SDCARD_ROOT + "/anybeenImageEditor/stickers/";

    // 图片保存路径
    String SAVE_DIR = SDCARD_ROOT + "/saves/";

    // 文件的子目录
    String ANIMAL           = STICKER + "dongwu";    // 动物
    String MOOD             = STICKER + "xinqing";   // 心情
    String COS              = STICKER + "cos";       // Cosplay
    String SYMBOL           = STICKER + "fuhao";     // 符号
    String DECORATION       = STICKER + "shipin";    // 饰品
    String SPRING_FESTIVAL  = STICKER + "chunjie";   // 春节
    String TEXT             = STICKER + "wenzi";     // 文字
    String NUMBER           = STICKER + "shuzi";     // 数字
    String FRAME            = STICKER + "biankuang"; // 边框
    String PROFESSION       = STICKER + "zhiye";     // 职业

    String TEMP = SDCARD_ROOT + "/anybeenImageEditor/stickers/";

}
