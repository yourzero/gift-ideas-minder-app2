package com.threekidsinatrenchcoat.giftideaminder.data.model

enum class PersonRole(val bit: Int) {
    SELF(1),
    RECIPIENT(2),
    GIFTER(4),
    COLLABORATOR(8),
    CONTACT_ONLY(16);

    companion object {
        fun fromInt(value: Int): Set<PersonRole> {
            return values().filter { (value and it.bit) != 0 }.toSet()
        }

        fun toInt(roles: Set<PersonRole>): Int {
            return roles.sumOf { it.bit }
        }

        fun Int.hasRole(role: PersonRole): Boolean = (this and role.bit) != 0

        fun Int.plusRole(role: PersonRole): Int = this or role.bit

        fun Int.minusRole(role: PersonRole): Int = this and role.bit.inv()
    }
}