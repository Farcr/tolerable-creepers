package com.evandev.tolerable_creepers.client.model;

import com.evandev.tolerable_creepers.Constants;
import com.evandev.tolerable_creepers.client.animation.CreepieAnimation;
import com.evandev.tolerable_creepers.common.entity.Creepie;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class CreepieModel<T extends Creepie> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "creepie"), "main");
    public static final ModelLayerLocation LAYER_LOCATION_ARMOR = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "creepie"), "armor");

    private final ModelPart root;
    private final ModelPart creepie;
    private final ModelPart r_leg_front;
    private final ModelPart l_leg_front;
    private final ModelPart r_leg_back;
    private final ModelPart l_leg_back;
    private final ModelPart upper_body;
    private final ModelPart head;
    private final ModelPart floof;

    public CreepieModel(ModelPart root) {
        this.root = root.getChild("root");
        this.creepie = this.root.getChild("creepie");
        this.r_leg_front = this.creepie.getChild("r_leg_front");
        this.l_leg_front = this.creepie.getChild("l_leg_front");
        this.r_leg_back = this.creepie.getChild("r_leg_back");
        this.l_leg_back = this.creepie.getChild("l_leg_back");
        this.upper_body = this.creepie.getChild("upper_body");
        this.head = this.upper_body.getChild("head");
        this.floof = this.head.getChild("floof");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition creepie = root.addOrReplaceChild("creepie", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition r_leg_front = creepie.addOrReplaceChild("r_leg_front", CubeListBuilder.create().texOffs(16, 12).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -3.0F, -2.0F));

        PartDefinition l_leg_front = creepie.addOrReplaceChild("l_leg_front", CubeListBuilder.create().texOffs(16, 12).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -3.0F, -2.0F));

        PartDefinition r_leg_back = creepie.addOrReplaceChild("r_leg_back", CubeListBuilder.create().texOffs(16, 17).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -3.0F, 2.0F));

        PartDefinition l_leg_back = creepie.addOrReplaceChild("l_leg_back", CubeListBuilder.create().texOffs(16, 17).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -3.0F, 2.0F));

        PartDefinition upper_body = creepie.addOrReplaceChild("upper_body", CubeListBuilder.create().texOffs(0, 12).addBox(-2.0F, -5.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, 0.0F));

        PartDefinition head = upper_body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -6.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 0.0F));

        PartDefinition floof = head.addOrReplaceChild("floof", CubeListBuilder.create().texOffs(0, 22).addBox(-3.0F, -6.0F, 0.0F, 6.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public @NotNull ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = headPitch * ((float) Math.PI / 180F);

        this.animate(entity.idleAnimationState, CreepieAnimation.creepie_idle, ageInTicks, 1.0F);
        this.animateWalk(CreepieAnimation.creepie_walk, limbSwing, limbSwingAmount, 2.0F, 2.5F);
        this.animate(entity.hurtAnimationState, CreepieAnimation.creepie_hurt, ageInTicks, 1.0F);
        this.animate(entity.sadAnimationState, CreepieAnimation.creepie_sad, ageInTicks, 1.0F);
        this.animate(entity.hideAnimationState, CreepieAnimation.creepie_hide, ageInTicks, 1.0F);
        this.animate(entity.danceAnimationState, CreepieAnimation.creepie_dance, ageInTicks, 1.0F);
    }
}