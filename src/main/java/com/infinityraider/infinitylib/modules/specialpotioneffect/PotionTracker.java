package com.infinityraider.infinitylib.modules.specialpotioneffect;

import com.infinityraider.infinitylib.utility.ISerializable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;

public class PotionTracker implements ISerializable {
    private EntityLivingBase entity;

    protected PotionTracker() {}

    public PotionTracker setEntity(EntityLivingBase entity) {
        if(this.entity == null) {
            this.entity = entity;
        }
        return this;
    }

    public EntityLivingBase getEntity() {
        return entity;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {

    }

    @Override
    public NBTTagCompound writeToNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        return tag;
    }
}
