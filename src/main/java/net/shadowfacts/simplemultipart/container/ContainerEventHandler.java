package net.shadowfacts.simplemultipart.container;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.shadowfacts.simplemultipart.util.MultipartHitResult;
import net.shadowfacts.simplemultipart.SimpleMultipart;
import net.shadowfacts.simplemultipart.util.MultipartHelper;

/**
 * @author shadowfacts
 */
public class ContainerEventHandler {

	public static void register() {
		AttackBlockCallback.EVENT.register(ContainerEventHandler::handleBlockAttack);
	}

	private static ActionResult handleBlockAttack(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) {
		if (world.isClient) {
			return ActionResult.PASS;
		}

		Block block = world.getBlockState(pos).getBlock();
		if (block != SimpleMultipart.containerBlock && block != SimpleMultipart.tickableContainerBlock) {
			return ActionResult.PASS;
		}

		MultipartContainer container = (MultipartContainer)world.getBlockEntity(pos);
		if (container == null) {
			return ActionResult.FAIL;
		}

		MultipartHitResult hit = MultipartHelper.rayTrace(container, world, pos, player);
		if (hit == null) {
			return ActionResult.FAIL;
		}

		boolean success = container.breakPart(hit.view, player);
		return success ? ActionResult.SUCCESS : ActionResult.FAIL;
	}

}
