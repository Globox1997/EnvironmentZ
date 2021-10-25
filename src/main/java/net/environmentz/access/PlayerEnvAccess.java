package net.environmentz.access;

public interface PlayerEnvAccess {

    public void setHotEnvAffected(boolean affected);

    public void setColdEnvAffected(boolean affected);

    public boolean isHotEnvAffected();

    public boolean isColdEnvAffected();

    public void compatSync();
}
