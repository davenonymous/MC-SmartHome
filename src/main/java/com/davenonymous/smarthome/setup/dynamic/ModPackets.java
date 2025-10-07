package com.davenonymous.smarthome.setup.dynamic;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.setup.dynamic.annotations.Packet;
import com.davenonymous.smarthome.setup.dynamic.annotations.PacketCodec;
import com.davenonymous.smarthome.setup.dynamic.annotations.PacketHandler;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforgespi.language.ModFileScanData;

import java.io.InvalidClassException;
import java.lang.annotation.ElementType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = SmartHome.MODID)
public class ModPackets {
	public static Map<String, CustomPacketPayload.Type<CustomPacketPayload>> TYPE_BY_CLASS = new HashMap<>();

	public static boolean DEBUG_PACKETS = false;

	@SubscribeEvent
	public static void register(final RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar("1");

		ModFileScanData scanData = ModList.get().getModFileById(SmartHome.MODID).getFile().getScanResult();
		var foundPacketClasses = scanData.getAnnotatedBy(Packet.class, ElementType.TYPE);

		foundPacketClasses.forEach(annotationData -> {

			try {
				var clazzName = annotationData.clazz().getClassName();
				var simpleName = clazzName.substring(clazzName.lastIndexOf('.') + 1).replace("Payload", "");
				var snakeCaseName = simpleName.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
				var newType = new CustomPacketPayload.Type<>(SmartHome.resource(snakeCaseName));

				Class<?> clazz = Class.forName(annotationData.clazz().getClassName());

				var handlerMethods = Arrays.stream(clazz.getDeclaredMethods()).filter(method -> method.isAnnotationPresent(PacketHandler.class)).toList();
				if(handlerMethods.isEmpty()) {
					throw new InvalidClassException("Packet class " + clazzName + " has no handler methods");
					//SmartHome.LOGGER.error("Packet class {} has no handler methods, skipping", clazzName);
					//return;
				}

				if(handlerMethods.size() > 1) {
					throw new InvalidClassException("Packet class " + clazzName + " has multiple handler methods");
				}

				var codecFields = Arrays.stream(clazz.getFields()).filter(field -> field.isAnnotationPresent(PacketCodec.class)).toList();
				if(codecFields.isEmpty()) {
					throw new InvalidClassException("Packet class " + clazzName + " has no codec field");
				}
				if(codecFields.size() > 1) {
					throw new InvalidClassException("Packet class " + clazzName + " has multiple codec fields");
				}

				var codecField = codecFields.getFirst();
				if(!StreamCodec.class.isAssignableFrom(codecField.getType())) {
					throw new InvalidClassException("Packet class " + clazzName + " has a codec field that is not a StreamCodec");
				}

				//noinspection rawtypes
				var codec = (StreamCodec)codecField.get(null);

				SmartHome.LOGGER.info("Registering packet: {} -> {}", simpleName, newType.id());
				TYPE_BY_CLASS.put(annotationData.clazz().getClassName(), newType);

				var method = handlerMethods.getFirst();
				var annotation = method.getAnnotation(PacketHandler.class);
				var handler = createHandler(method);
				if(annotation.value() == PacketHandler.Receiver.Client) {
					//noinspection unchecked
					registrar.playToClient(newType, codec, handler);
				} else if(annotation.value() == PacketHandler.Receiver.Server) {
					//noinspection unchecked
					registrar.playToServer(newType, codec, handler);
				} else if(annotation.value() == PacketHandler.Receiver.Both) {
					//noinspection unchecked
					registrar.playBidirectional(newType, codec, handler);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}

	@SuppressWarnings("rawtypes")
	private static IPayloadHandler createHandler(Method method) {
		return (customPacketPayload, iPayloadContext) -> {
			try {
				if(DEBUG_PACKETS) {
					SmartHome.LOGGER.debug("Invoking packet handler: {}.{}()", method.getDeclaringClass().getSimpleName(), method.getName());
					for(var field : customPacketPayload.getClass().getDeclaredFields()) {
						if(Modifier.isStatic(field.getModifiers())) {
							continue;
						}

						var accessor = customPacketPayload.getClass().getDeclaredMethod(field.getName());
						var value = accessor.invoke(customPacketPayload);
						SmartHome.LOGGER.debug("  {} {} = {}", field.getType().getSimpleName(), field.getName(), value);
					}
					SmartHome.LOGGER.debug("done");
				}
				method.invoke(null, customPacketPayload, iPayloadContext);
			} catch (IllegalAccessException | InvocationTargetException e) {
				SmartHome.LOGGER.error("Failed to handle packet {}", customPacketPayload.getClass().getName(), e);
			} catch (NoSuchMethodException e) {
				SmartHome.LOGGER.error("Could not find value accessor for {}", customPacketPayload.getClass().getName(), e);
			}
		};
	}

}
