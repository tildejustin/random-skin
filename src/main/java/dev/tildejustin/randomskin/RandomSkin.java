package dev.tildejustin.randomskin;

import dev.tildejustin.randomskin.mixin.accessor.MinecraftClientAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class RandomSkin {
    private static final Identifier STEVE_SKIN = new Identifier("textures/entity/steve.png");
    private static final Identifier ALEX_SKIN = new Identifier("textures/entity/alex.png");
    public static Identifier skin;
    public static boolean texturesLoaded = false;
    public static MinecraftClient client = MinecraftClient.getInstance();
    public static ResourceManager resourceManager = client.getResourceManager();
    public static TextureManager textureManager = client.getTextureManager();
    public static File skinCacheDir = new File(((MinecraftClientAccessor) client).getAssetDirectory(), "skins");
    public static List<File> skinCache = null;
    public static Random random = new Random();
    public static String model = "default";
    public static Mode mode = Mode.cached;
    public static File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "random-skin.properties");

    static {
        try {
            if (configFile.createNewFile()) {
                RandomSkin.saveProperties();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        RandomSkin.loadProperties();
    }

    public static void saveProperties() {
        Properties config = new Properties();
        try (FileWriter f = new FileWriter(configFile)) {
            config.put("mode", mode.name());
            config.store(f, "Options\nMode: cached, random");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadProperties() {
        Properties config = new Properties();
        try (FileInputStream f = new FileInputStream(configFile)) {
            config.load(f);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mode = config.getProperty("mode", Mode.cached.name()).equals(Mode.random.name()) ? Mode.random : Mode.cached;
    }

    public static void setNextSkin() {
        UUID tempUUID = UUID.randomUUID();
        RandomSkin.skin = new Identifier("random-skin", "textures/generated/" + tempUUID);
        switch (mode) {
            case cached:
                textureManager.loadTexture(skin, new FakePlayerSkinTexture(RandomSkin.randomCachedSkin(), skin));
                break;
            case random:
                textureManager.loadTexture(skin, new FakePlayerSkinTexture(RandomSkin.randomPixelSkin(tempUUID), skin));
                break;
        }

    }

    public static boolean shouldUseSlimModel(UUID uuid) {
        return (uuid.hashCode() & 1) == 1;
    }

    private static BufferedImage randomPixelSkin(UUID uuid) {
        BufferedImage texture;
        model = RandomSkin.shouldUseSlimModel(uuid) ? "slim" : "default";
        try {
            texture = TextureUtil.create(resourceManager.getResource(RandomSkin.shouldUseSlimModel(uuid) ? ALEX_SKIN : STEVE_SKIN).getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (int x = 0; x < texture.getWidth(); x++) {
            for (int y = 0; y < texture.getHeight(); y++) {
                if (((texture.getRGB(x, y) >> 24) & 0xFF) != 0) {
                    texture.setRGB(x, y, (random.nextInt() & 0x00FFFFFF) + 0xFF000000);
                }
            }
        }
        return texture;
    }

    private static File randomCachedSkin() {
        File skinFile = null;
        BufferedImage skinFileImage = null;
        while (skinFile == null) {
            if (skinCache == null) {
                skinCache = RandomSkin.cacheSkins();
            }
            skinFile = skinCache.get(random.nextInt(skinCache.size()));

            try {
                skinFileImage = ImageIO.read(skinFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (
                    skinFileImage.getHeight() != 64 ||
                            skinFileImage.getWidth() != 64 ||
                            ((skinFileImage.getRGB(45, 52) >> 24) & 0xFF) != 0xFF ||
                            (skinFileImage.getRGB(45, 52) & 0xFFFFFFF) == 0x00FF00 ||
                            (((skinFileImage.getRGB(25, 22) & 0xFF) == 0) &&
                                    ((skinFileImage.getRGB(25, 22) >> 8) & 0xFF) == 0 &&
                                    ((skinFileImage.getRGB(25, 22) >> 16) & 0xFF) == 0)
            ) {
                skinFile = null;
            }
        }
        // if this arm pixel is transparent, it should be slim
        RandomSkin.model = (((skinFileImage.getRGB(46, 52) >> 24) & 0xFF) == 0 || (skinFileImage.getRGB(46, 52) & 0xFFFFFF00) == 0x000000) ? "slim" : "default";

        System.out.println(skinFile);
        return skinFile;
    }

    private static List<File> cacheSkins() {
        List<File> files = new ArrayList<>();
        try {
            Files.walkFileTree(skinCacheDir.toPath(), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (file.toFile().isFile() && file.toFile().getName().split("\\.").length < 2)
                        files.add(file.toFile());
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(files.size());
        return files;
    }

    public enum Mode {
        cached, random;
    }
}
