package android.content.pm;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

import java.util.List;

public interface IPackageManager extends IInterface {

    ParceledListSlice<ApplicationInfo> getInstalledApplications(int flags, int userId)
            throws RemoteException;

    ParceledListSlice<ApplicationInfo> getInstalledApplications(long flags, int userId)
            throws RemoteException;

    int installExistingPackageAsUser(String packageName, int userId, int installFlags,
                                     int installReason, List<String> whiteListedPermissions);

    abstract class Stub extends Binder implements IPackageManager {

        public static IPackageManager asInterface(IBinder obj) {
            throw new UnsupportedOperationException();
        }
    }
}
