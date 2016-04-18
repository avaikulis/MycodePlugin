package technium.treasurechest;

import org.bukkit.permissions.Permission;

public class Permissions
{
    public Permission admin;
    
    public Permissions() {
        this.admin = new Permission("tc.admin");
    }
}
