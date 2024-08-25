package dev.tildejustin.randomskin;

import net.minecraft.client.render.BufferedImageSkinProvider;
import net.minecraft.client.render.DownloadedSkinParser;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FakePlayerSkinTexture extends ResourceTexture {
    private File file = null;
    private BufferedImage image = null;
    private boolean loaded = false;
    private final BufferedImageSkinProvider bufferedImageSkinProvider = new DownloadedSkinParser();

    public FakePlayerSkinTexture(File file, Identifier identifier) {
        super(identifier);
        this.file = file;
    }

    public FakePlayerSkinTexture(BufferedImage image, Identifier identifier) {
        super(identifier);
        this.image = image;
    }

    @Override
    public void load(ResourceManager manager) throws IOException {
        // skin should already in texture map, possibly manually entered
        if (this.image == null && this.file == null) {
            super.load(manager);
        } else if (this.file != null && this.image == null) {
            // this.image = this.bufferedImageSkinProvider.parseSkin(ImageIO.read(this.file));
            this.image = ImageIO.read(this.file);
        }
    }

    @Override
    public int getGlId() {
        this.getGLIdInternal();
        return super.getGlId();
    }

    private void getGLIdInternal() {
        if (!this.loaded) {
            if (this.image != null) {
                // identifier
                if (this.field_6555 != null) {
                    this.clearGlId();
                }
                TextureUtil.method_5858(super.getGlId(), this.image);
                this.loaded = true;
            }
        }
    }
}
