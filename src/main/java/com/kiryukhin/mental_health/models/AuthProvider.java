package com.kiryukhin.mental_health.models;

import jakarta.persistence.Table;

@Table(name = "auth_providers")
public enum AuthProvider {
    LOCAL, VK, GOOGLE
}
