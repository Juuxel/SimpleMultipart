package net.shadowfacts.simplemultipart.test;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.StringTextComponent;
import net.minecraft.util.Hand;
import net.minecraft.util.Tickable;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.shadowfacts.simplemultipart.api.MultipartContainer;
import net.shadowfacts.simplemultipart.api.MultipartView;
import net.shadowfacts.simplemultipart.multipart.Multipart;
import net.shadowfacts.simplemultipart.multipart.MultipartState;
import net.shadowfacts.simplemultipart.multipart.entity.MultipartEntity;
import net.shadowfacts.simplemultipart.multipart.entity.MultipartEntityProvider;

/**
 * @author shadowfacts
 */
public class TickableEntityTestPart extends Multipart implements MultipartEntityProvider {

	@Override
	@Deprecated
	public VoxelShape getBoundingShape(MultipartState state, MultipartView view) {
		return VoxelShapes.cube(6/16f, 6/16f, 6/16f, 10/16f, 10/16f, 10/16f);
	}

	@Override
	@Deprecated
	public boolean activate(MultipartState state, MultipartView view, PlayerEntity player, Hand hand) {
		int timer = ((Entity)view.getEntity()).timer;
		player.addChatMessage(new StringTextComponent("Timer: " + timer), false);
		return true;
	}

	@Override
	public MultipartEntity createMultipartEntity(MultipartState state, MultipartContainer container) {
		return new Entity(container);
	}

	public static class Entity extends MultipartEntity implements Tickable {
		public int timer = 0;

		public Entity(MultipartContainer container) {
			super(container);
		}

		@Override
		public void tick() {
			timer++;
		}
	}

}