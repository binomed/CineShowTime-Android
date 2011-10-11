/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\tools\\projects\\CineShowTime-Android\\Libraries\\CineShowTime\\src\\com\\binomed\\showtime\\android\\screen\\movie\\ICallbackMovie.aidl
 */
package com.binomed.showtime.android.screen.movie;
public interface ICallbackMovie extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.binomed.showtime.android.screen.movie.ICallbackMovie
{
private static final java.lang.String DESCRIPTOR = "com.binomed.showtime.android.screen.movie.ICallbackMovie";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.binomed.showtime.android.screen.movie.ICallbackMovie interface,
 * generating a proxy if needed.
 */
public static com.binomed.showtime.android.screen.movie.ICallbackMovie asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.binomed.showtime.android.screen.movie.ICallbackMovie))) {
return ((com.binomed.showtime.android.screen.movie.ICallbackMovie)iin);
}
return new com.binomed.showtime.android.screen.movie.ICallbackMovie.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_finish:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.finish(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.binomed.showtime.android.screen.movie.ICallbackMovie
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public void finish(java.lang.String idMovie) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(idMovie);
mRemote.transact(Stub.TRANSACTION_finish, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_finish = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public void finish(java.lang.String idMovie) throws android.os.RemoteException;
}
