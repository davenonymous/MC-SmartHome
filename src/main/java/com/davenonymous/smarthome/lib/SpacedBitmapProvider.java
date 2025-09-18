package com.davenonymous.smarthome.lib;

import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.SheetGlyphInfo;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import net.minecraft.client.gui.font.CodepointMap;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.providers.GlyphProviderDefinition;
import net.minecraft.client.gui.font.providers.GlyphProviderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class SpacedBitmapProvider implements GlyphProvider {
    static final Logger LOGGER = LogUtils.getLogger();
    private final NativeImage image;
    private final CodepointMap<Glyph> glyphs;

    SpacedBitmapProvider(NativeImage image, CodepointMap<Glyph> glyphs) {
        this.image = image;
        this.glyphs = glyphs;
    }

    @Override
    public void close() {
        this.image.close();
    }

    @Nullable
    @Override
    public GlyphInfo getGlyph(int character) {
        return this.glyphs.get(character);
    }

    @Override
    public IntSet getSupportedGlyphs() {
        return IntSets.unmodifiable(this.glyphs.keySet());
    }

    @OnlyIn(Dist.CLIENT)
    public static record Definition(ResourceLocation file, int height, int ascent, int[][] codepointGrid, Map<Integer, Float> advances) implements GlyphProviderDefinition {
        private static final Codec<int[][]> CODEPOINT_GRID_CODEC = Codec.STRING.listOf().xmap(p_286900_ -> {
            int i = p_286900_.size();
            int[][] aint = new int[i][];

            for (int j = 0; j < i; j++) {
                aint[j] = p_286900_.get(j).codePoints().toArray();
            }

            return aint;
        }, p_286828_ -> {
            List<String> list = new ArrayList<>(p_286828_.length);

            for (int[] aint : p_286828_) {
                list.add(new String(aint, 0, aint.length));
            }

            return list;
        }).validate(Definition::validateDimensions);

		public static final MapCodec<Definition> PSEUDO_LIST_CODEC = RecordCodecBuilder.<Definition>mapCodec(
				p_286905_ -> p_286905_.group(
						Codec.STRING.fieldOf("type").forGetter(def -> "smarthome:spaced_bitmap"),
						ResourceLocation.CODEC.fieldOf("file").forGetter(Definition::file),
						Codec.INT.optionalFieldOf("height", Integer.valueOf(8)).forGetter(Definition::height),
						Codec.INT.fieldOf("ascent").forGetter(Definition::ascent),
						CODEPOINT_GRID_CODEC.fieldOf("chars").forGetter(Definition::codepointGrid),
						Codec.unboundedMap(ExtraCodecs.CODEPOINT, Codec.FLOAT).fieldOf("advances").forGetter(Definition::advances)
					)
					.apply(p_286905_, (s, resourceLocation, integer, integer2, ints, integerFloatMap) -> new Definition(resourceLocation, integer, integer2, ints, integerFloatMap))
			)
			.validate(Definition::validate);

        public static final MapCodec<Definition> CODEC = RecordCodecBuilder.<Definition>mapCodec(
                p_286905_ -> p_286905_.group(
                            ResourceLocation.CODEC.fieldOf("file").forGetter(Definition::file),
                            Codec.INT.optionalFieldOf("height", Integer.valueOf(8)).forGetter(Definition::height),
                            Codec.INT.fieldOf("ascent").forGetter(Definition::ascent),
                            CODEPOINT_GRID_CODEC.fieldOf("chars").forGetter(Definition::codepointGrid),
							Codec.unboundedMap(ExtraCodecs.CODEPOINT, Codec.FLOAT).fieldOf("advances").forGetter(Definition::advances)
                        )
                        .apply(p_286905_, Definition::new)
            )
            .validate(Definition::validate);

        private static DataResult<int[][]> validateDimensions(int[][] dimensions) {
            int i = dimensions.length;
            if (i == 0) {
                return DataResult.error(() -> "Expected to find data in codepoint grid");
            } else {
                int[] aint = dimensions[0];
                int j = aint.length;
                if (j == 0) {
                    return DataResult.error(() -> "Expected to find data in codepoint grid");
                } else {
                    for (int k = 1; k < i; k++) {
                        int[] aint1 = dimensions[k];
                        if (aint1.length != j) {
                            return DataResult.error(
                                () -> "Lines in codepoint grid have to be the same length (found: "
                                        + aint1.length
                                        + " codepoints, expected: "
                                        + j
                                        + "), pad with \\u0000"
                            );
                        }
                    }

                    return DataResult.success(dimensions);
                }
            }
        }

        private static DataResult<Definition> validate(Definition definition) {
            return definition.ascent > definition.height
                ? DataResult.error(() -> "Ascent " + definition.ascent + " higher than height " + definition.height)
                : DataResult.success(definition);
        }

        @Override
        public GlyphProviderType type() {
            return GlyphProviderType.BITMAP;
        }

        @Override
        public Either<Loader, Reference> unpack() {
            return Either.left(this::load);
        }

        private GlyphProvider load(ResourceManager resoureManager) throws IOException {
            ResourceLocation resourcelocation = this.file.withPrefix("textures/");

            SpacedBitmapProvider bitmapprovider;
            try (InputStream inputstream = resoureManager.open(resourcelocation)) {
                NativeImage nativeimage = NativeImage.read(NativeImage.Format.RGBA, inputstream);
                int imageWidth = nativeimage.getWidth();
                int imageHeight = nativeimage.getHeight();
                int glyphWidth = imageWidth / this.codepointGrid[0].length;
                int glyphHeight = imageHeight / this.codepointGrid.length;
                float f = (float)this.height / (float)glyphHeight;
                CodepointMap<Glyph> codepointmap = new CodepointMap<>(Glyph[]::new, Glyph[][]::new);

                for (int row = 0; row < this.codepointGrid.length; row++) {
                    int colPosition = 0;

                    for (int col : this.codepointGrid[row]) {
                        int l1 = colPosition++;
                        if (col != 0) {
                            int actualGlyphWidth = Math.round(advances.getOrDefault(col, (float)this.getActualGlyphWidth(nativeimage, glyphWidth, glyphHeight, l1, row)));
							int offsetY = row * glyphHeight;
                            Glyph bitmapprovider$glyph = codepointmap.put(
                                col, new Glyph(f, nativeimage, l1 * glyphWidth, offsetY, glyphWidth, glyphHeight, (int)(0.5 + (double)((float)actualGlyphWidth * f)) + 1, this.ascent)
                            );
                            if (bitmapprovider$glyph != null) {
                                SpacedBitmapProvider.LOGGER.warn("Codepoint '{}' declared multiple times in {}", Integer.toHexString(col), resourcelocation);
                            }
                        }
                    }
                }

                bitmapprovider = new SpacedBitmapProvider(nativeimage, codepointmap);
            }

            return bitmapprovider;
        }

        private int getActualGlyphWidth(NativeImage image, int width, int height, int x, int y) {
            int i;
            for (i = width - 1; i >= 0; i--) {
                int j = x * width + i;

                for (int k = 0; k < height; k++) {
                    int l = y * height + k;
                    if (image.getLuminanceOrAlpha(j, l) != 0) {
                        return i + 1;
                    }
                }
            }

            return i + 1;
        }
    }

    @OnlyIn(Dist.CLIENT)
    static record Glyph(float scale, NativeImage image, int offsetX, int offsetY, int width, int height, int advance, int ascent) implements GlyphInfo {
        @Override
        public float getAdvance() {
            return (float)this.advance;
        }

        @Override
        public BakedGlyph bake(Function<SheetGlyphInfo, BakedGlyph> glyphProvider) {
            return glyphProvider.apply(new SheetGlyphInfo() {
                @Override
                public float getOversample() {
                    return 1.0F / Glyph.this.scale;
                }

                @Override
                public int getPixelWidth() {
                    return Glyph.this.width;
                }

                @Override
                public int getPixelHeight() {
                    return Glyph.this.height;
                }

                @Override
                public float getBearingTop() {
                    return (float)Glyph.this.ascent;
                }

                @Override
                public void upload(int p_232658_, int p_232659_) {
                    Glyph.this.image.upload(0, p_232658_, p_232659_, Glyph.this.offsetX, Glyph.this.offsetY, Glyph.this.width, Glyph.this.height, false, false);
                }

                @Override
                public boolean isColored() {
                    return Glyph.this.image.format().components() > 1;
                }
            });
        }
    }
}
