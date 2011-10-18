/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\tools\\projects\\CineShowTime-Android\\Libraries\\CineShowTime\\src\\com\\binomed\\showtime\\android\\screen\\movie\\IServiceMovie.aidl
 */
package com.binomed.showtime.android.screen.movie;
public interface IServiceMovie extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.binomed.showtime.android.screen.movie.IServiceMovie
{
private static final java.lang.String DESCRIPTOR = "com.binomed.showtime.android.screen.movie.IServiceMovie";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.binomed.showtime.android.screen.movie.IServiceMovie interface,
 * generating a proxy if needed.
 */
public static com.binomed.showtime.android.screen.movie.IServiceMovie asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.binomed.showtime.android.screen.movie.IServiceMovie))) {
return ((com.binomed.showtime.android.screen.movie.IServiceMovie)iin);
}
return new com.binomed.showtime.android.screen.movie.IServiceMovie.Stub.Proxy(obj);
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
case TRANSACTION_getMovie:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
com.binomed.showtime.android.model.MovieBean _result = this.getMovie(_arg0);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_registerCallback:
{
data.enforceInterface(DESCRIPTOR);
com.binomed.showtime.android.screen.movie.ICallbackMovie _arg0;
_arg0 = com.binomed.showtime.android.screen.movie.ICallbackMovie.Stub.asInterface(data.readStrongBinder());
this.registerCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unregisterCallback:
{
data.enforceInterface(DESCRIPTOR);
com.binomed.showtime.android.screen.movie.ICallbackMovie _arg0;
_arg0 = com.binomed.showtime.android.screen.movie.ICallbackMovie.Stub.asInterface(data.readStrongBinder());
this.unregisterCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_isServiceRunning:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isServiceRunning();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_cancelService:
{
data.enforceInterface(DESCRIPTOR);
this.cancelService();
reply.writeNoException();
return true;
}
case TRANSACTION_error:
{
data.enforceInterface(DESCRIPTOR);
this.error();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.binomed.showtime.android.screen.movie.IServiceMovie
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
public void finish(java.lang.String movieId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(movieId);
mRemote.transact(Stub.TRANSACTION_finish, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public com.binomed.showtime.android.model.MovieBean getMovie(java.lang.String movieId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.binomed.showtime.android.model.MovieBean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(movieId);
mRemote.transact(Stub.TRANSACTION_getMovie, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = com.binomed.showtime.android.model.MovieBean.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void registerCallback(com.binomed.showtime.android.screen.movie.ICallbackMovie cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void unregisterCallback(com.binomed.showtime.android.screen.movie.ICallbackMovie cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean isServiceRunning() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isServiceRunning, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void cancelService() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_cancelService, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void error() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_error, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_finish = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_getMovie = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_registerCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_unregisterCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_isServiceRunning = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_cancelService = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_error = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
}
public void finish(java.lang.String movieId) throws android.os.RemoteException;
public com.binomed.showtime.android.model.MovieBean getMovie(java.lang.String movieId) throws android.os.RemoteException;
public void registerCallback(com.binomed.showtime.android.screen.movie.ICallbackMovie cb) throws android.os.RemoteException;
public void unregisterCallback(com.binomed.showtime.android.screen.movie.ICallbackMovie cb) throws android.os.RemoteException;
public boolean isServiceRunning() throws android.os.RemoteException;
public void cancelService() throws android.os.RemoteException;
public void error() throws android.os.RemoteException;
}
