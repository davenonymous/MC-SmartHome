package com.davenonymous.smarthome.setup.dynamic;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import net.minecraft.data.PackOutput;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforgespi.language.ModFileScanData;

import java.lang.annotation.ElementType;

public class ModTranslations extends LanguageProvider {
	private final String modid;
	private final String locale;

	public ModTranslations(PackOutput output, String modid, String locale) {
		super(output, modid, locale);
		this.modid = modid;
		this.locale = locale;
	}

	@Override
	protected void addTranslations() {
		ModFileScanData scanData = ModList.get().getModFileById(SmartHome.MODID).getFile().getScanResult();
		var foundTranslatedFields = scanData.getAnnotatedBy(I18DataGen.class, ElementType.FIELD);

		foundTranslatedFields.forEach(annotationData -> {
			String lang = (String) annotationData.annotationData().get("lang");
			if(!lang.equals(this.locale)) {
				return;
			}

			String translation = (String) annotationData.annotationData().get("string");
			String key = "";
			try {
				var clazz = Class.forName(annotationData.clazz().getClassName());
				var field = clazz.getDeclaredField(annotationData.memberName());
				if(I18String.class.isAssignableFrom(field.getType())) {
					I18String fieldValue = (I18String)field.get(null);
					key = fieldValue.key();
				}
			} catch (IllegalAccessException | ClassNotFoundException | NoSuchFieldException e) {
				return;
			}

			if(key.isEmpty()) {
				return;
			}

			SmartHome.LOGGER.info("Found translations for field: key={}, locale={} -> {}", key, lang, translation);
			this.add(key, translation);
		});
	}
}
