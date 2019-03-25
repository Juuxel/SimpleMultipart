package net.shadowfacts.simplemultipart;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.loot.context.LootContextParameter;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.world.loot.context.LootContextType;
import net.minecraft.world.loot.context.LootContextTypes;
import net.shadowfacts.simplemultipart.container.*;
import net.shadowfacts.simplemultipart.multipart.Multipart;
import net.shadowfacts.simplemultipart.multipart.MultipartState;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author shadowfacts
 */
public class SimpleMultipart implements ModInitializer {

	public static final String MODID = "simplemultipart";

	public static final Registry<Multipart> MULTIPART = createMultipartRegistry();

	private static final BiFunction<String, Consumer<LootContextType.Builder>, LootContextType> LOOT_CONTEXT_REGISTER_FUNCTION =
			findLootContextRegisterFunction();

	public static final LootContextParameter<MultipartState> MULTIPART_STATE_PARAMETER = new LootContextParameter<>(new Identifier(MODID, "multipart_state"));
	public static final LootContextType MULTIPART_LOOT_CONTEXT = createMultipartLootContextType();

	public static final ContainerBlock containerBlock = new ContainerBlock();
	public static final TickableContainerBlock tickableContainerBlock = new TickableContainerBlock();
	public static final BlockEntityType<ContainerBlockEntity> containerBlockEntity = createBlockEntityType("container", ContainerBlockEntity::new);
	public static final BlockEntityType<TickableContainerBlockEntity> tickableContainerBlockEntity = createBlockEntityType("tickable_container", TickableContainerBlockEntity::new);

	@Override
	public void onInitialize() {
		Registry.register(Registry.BLOCK, new Identifier(MODID, "container"), containerBlock);
		Registry.register(Registry.BLOCK, new Identifier(MODID, "tickable_container"), tickableContainerBlock);

		ContainerEventHandler.register();
	}

	private static Registry<Multipart> createMultipartRegistry() {
		SimpleRegistry<Multipart> registry = new SimpleRegistry<>();
		Registry.REGISTRIES.add(new Identifier(MODID, "multipart"), registry);
		return registry;
	}

	private static <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(String name, Supplier<T> supplier) {
		BlockEntityType.Builder<T> builder = BlockEntityType.Builder.create(supplier);
		return Registry.register(Registry.BLOCK_ENTITY, new Identifier(MODID, name), builder.build(null));
	}

	private static LootContextType createMultipartLootContextType() {
		try {
			// Load the function
			Class.forName(LootContextTypes.class.getCanonicalName());
		} catch (ClassNotFoundException e) {}
		return LOOT_CONTEXT_REGISTER_FUNCTION.apply(
				MODID + ":multipart",
				builder -> builder.require(MULTIPART_STATE_PARAMETER).require(LootContextParameters.POSITION)
		);
	}

	private static BiFunction<String, Consumer<LootContextType.Builder>, LootContextType> findLootContextRegisterFunction() {
		Class<?>[] targetParams = { String.class, Consumer.class };
		Method target = null;

		for (Method m : LootContextTypes.class.getDeclaredMethods()) {
			if (Arrays.equals(m.getParameterTypes(), targetParams)) {
				target = m;
				break;
			}
		}

		if (target != null) {
			final Method finalTarget = target;
			finalTarget.setAccessible(true);
			return (s, c) -> {
				try {
					return (LootContextType) finalTarget.invoke(null, s, c);
				} catch (IllegalAccessException | InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			};
		} else {
			throw new RuntimeException("Could not find registration method in LootContextTypes!");
		}
	}
}
