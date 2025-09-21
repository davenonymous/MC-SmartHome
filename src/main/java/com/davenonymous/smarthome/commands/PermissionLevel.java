package com.davenonymous.smarthome.commands;

import net.minecraft.commands.CommandSourceStack;

import java.util.function.Predicate;

public enum PermissionLevel {
	ANYONE(0, "anyone"),
	MODERATOR(1, "moderator"),
	GAMEMASTER(2, "gamemaster"),
	ADMIN(3, "admin"),
	OWNER(4, "owner");

	public final int level;
	public final String id;

	PermissionLevel(int level, String id) {
		this.level = level;
		this.id = id;
	}

	public static Predicate<CommandSourceStack> isAtLeast(PermissionLevel level) {
		return commandSourceStack -> commandSourceStack.hasPermission(level.level);
	}

	public static Predicate<CommandSourceStack> isExactly(PermissionLevel level) {
		return commandSourceStack -> commandSourceStack.hasPermission(level.level) && !commandSourceStack.hasPermission(level.level + 1);
	}

	public static Predicate<CommandSourceStack> isAnyone() {
		return commandSourceStack -> true;
	}

	public static Predicate<CommandSourceStack> isModerator() {
		return isAtLeast(MODERATOR);
	}

	public static Predicate<CommandSourceStack> isGameMaster() {
		return isAtLeast(GAMEMASTER);
	}

	public static Predicate<CommandSourceStack> isAdmin() {
		return isAtLeast(ADMIN);
	}

	public static Predicate<CommandSourceStack> isOwner() {
		return isAtLeast(OWNER);
	}
}
