package dev.gwgs.realisticbloodphysics.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import javax.annotation.Nullable;

public class BloodParticles extends TextureSheetParticle {

    protected BloodParticles(ClientLevel level, double x, double y, double z, SpriteSet spriteSet, double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.setSpriteFromAge(spriteSet);
        this.hasPhysics = true;
        this.gravity = 2.4f;
        this.lifetime = 500;
        this.setSize(0.3f, 0.3f);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double pX, double pY,
                                       double pZ, double pXSpeed, double pYSpeed,
                                       double pZSpeed) {
            return new BloodParticles(clientLevel, pX, pY, pZ, this.spriteSet, pXSpeed, pYSpeed, pZSpeed);
        }
    }
}
