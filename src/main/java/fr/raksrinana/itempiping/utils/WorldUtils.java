package fr.raksrinana.itempiping.utils;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WorldUtils{
	public static void popItemstackInWorld(@Nonnull World world, @Nonnull BlockPos pos, @Nullable Direction direction, @Nonnull ItemStack stack){
		double x, y, z, xVel, yVel, zVel;
		BlockPos output_pos = pos.offset(direction);
		double xOff = direction.getXOffset();
		double yOff = direction.getYOffset();
		double zOff = direction.getZOffset();
		if(!world.getBlockState(output_pos).isSolid()){
			x = pos.getX() + 0.5D + xOff * 0.75D;
			y = pos.getY() + 0.25D + yOff * 0.75D;
			z = pos.getZ() + 0.5D + zOff * 0.75D;
			xVel = xOff * 0.1D;
			yVel = yOff * 0.1D;
			zVel = zOff * 0.1D;
		}
		else
		{
			x = pos.getX() + 0.5D;
			y = pos.getY() + 0.5D;
			z = pos.getZ() + 0.5D;
			xVel = 0D;
			yVel = 0D;
			zVel = 0D;
		}
		ItemEntity itementity = new ItemEntity(world, x, y, z, stack);
		itementity.setDefaultPickupDelay();
		itementity.setMotion(xVel, yVel, zVel);
		world.addEntity(itementity);
	}
}
