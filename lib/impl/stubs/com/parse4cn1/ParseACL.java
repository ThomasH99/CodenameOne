package com.parse4cn1;


/**
 *  A {@code ParseACL} is used to control which users can access or modify a
 *  particular object. Each {@link ParseObject} can have its own
 *  {@code ParseACL}. You can grant read and write permissions separately to
 *  specific users, to groups of users that belong to roles, or you can grant
 *  permissions to "the public" so that, for example, any user could read a
 *  particular object but only a particular set of users could write to that
 *  object.
 */
public class ParseACL implements com.codename1.io.Externalizable {

	public static final String CLASS_NAME = "ParseACL";

	/**
	 *  Creates an ACL with no permissions granted.
	 */
	public ParseACL() {
	}

	/**
	 *  Creates an ACL where only the provided user has access.
	 * 
	 *  @param owner The only user that can read or write objects governed by
	 *  this ACL.
	 */
	public ParseACL(ParseUser owner) {
	}

	/**
	 *  Sets a default ACL that will be applied to all {@link ParseObject}s when
	 *  they are created.
	 * 
	 *  @param acl The ACL to use as a template for all {@link ParseObject}s
	 *  created after setDefaultACL has been called. This value will be copied
	 *  and used as a template for the creation of new ACLs, so changes to the
	 *  instance after {@code setDefaultACL(ParseACL, boolean)} has been called
	 *  will not be reflected in new {@link ParseObject}s.
	 *  @param withAccessForCurrentUser If {@code true}, the {@code ParseACL}
	 *  that is applied to newly-created {@link ParseObject}s will provide read
	 *  and write access to the {@link ParseUser#getCurrentUser()} at the time of
	 *  creation. If {@code false}, the provided ACL will be used without
	 *  modification. If acl is {@code null}, this value is ignored.
	 */
	public static void setDefaultACL(ParseACL acl, boolean withAccessForCurrentUser) {
	}

	@java.lang.Override
	public int getVersion() {
	}

	@java.lang.Override
	public void externalize(java.io.DataOutputStream out) {
	}

	@java.lang.Override
	public void internalize(int version, java.io.DataInputStream in) {
	}

	@java.lang.Override
	public String getObjectId() {
	}

	public ca.weblite.codename1.json.JSONObject encode(encode.IParseObjectEncodingStrategy objectEncoder) {
	}

	/**
	 *  Set whether the public is allowed to read this object.
	 */
	public void setPublicReadAccess(boolean allowed) {
	}

	/**
	 *  Get whether the public is allowed to read this object.
	 */
	public boolean getPublicReadAccess() {
	}

	/**
	 *  Set whether the public is allowed to write this object.
	 */
	public void setPublicWriteAccess(boolean allowed) {
	}

	/**
	 *  Set whether the public is allowed to write this object.
	 */
	public boolean getPublicWriteAccess() {
	}

	/**
	 *  Set whether the given user id is allowed to read this object.
	 */
	public void setReadAccess(String userId, boolean allowed) {
	}

	/**
	 *  Get whether the given user id is *explicitly* allowed to read this
	 *  object. Even if this returns {@code false}, the user may still be able to
	 *  access it if getPublicReadAccess returns {@code true} or a role that the
	 *  user belongs to has read access.
	 */
	public boolean getReadAccess(String userId) {
	}

	/**
	 *  Set whether the given user id is allowed to write this object.
	 */
	public void setWriteAccess(String userId, boolean allowed) {
	}

	/**
	 *  Get whether the given user id is *explicitly* allowed to write this
	 *  object. Even if this returns {@code false}, the user may still be able to
	 *  write it if getPublicWriteAccess returns {@code true} or a role that the
	 *  user belongs to has write access.
	 */
	public boolean getWriteAccess(String userId) {
	}

	/**
	 *  Set whether the given user is allowed to read this object.
	 */
	public void setReadAccess(ParseUser user, boolean allowed) {
	}

	/**
	 *  Get whether the given user id is *explicitly* allowed to read this
	 *  object. Even if this returns {@code false}, the user may still be able to
	 *  access it if getPublicReadAccess returns {@code true} or a role that the
	 *  user belongs to has read access.
	 */
	public boolean getReadAccess(ParseUser user) {
	}

	/**
	 *  Set whether the given user is allowed to write this object.
	 */
	public void setWriteAccess(ParseUser user, boolean allowed) {
	}

	/**
	 *  Get whether the given user id is *explicitly* allowed to write this
	 *  object. Even if this returns {@code false}, the user may still be able to
	 *  write it if getPublicWriteAccess returns {@code true} or a role that the
	 *  user belongs to has write access.
	 */
	public boolean getWriteAccess(ParseUser user) {
	}

	/**
	 *  Get whether users belonging to the role with the given roleName are
	 *  allowed to read this object. Even if this returns {@code false}, the role
	 *  may still be able to read it if a parent role has read access.
	 * 
	 *  @param roleName The name of the role.
	 *  @return {@code true} if the role has read access. {@code false}
	 *  otherwise.
	 */
	public boolean getRoleReadAccess(String roleName) {
	}

	/**
	 *  Set whether users belonging to the role with the given roleName are
	 *  allowed to read this object.
	 * 
	 *  @param roleName The name of the role.
	 *  @param allowed Whether the given role can read this object.
	 */
	public void setRoleReadAccess(String roleName, boolean allowed) {
	}

	/**
	 *  Get whether users belonging to the role with the given roleName are
	 *  allowed to write this object. Even if this returns {@code false}, the
	 *  role may still be able to write it if a parent role has write access.
	 * 
	 *  @param roleName The name of the role.
	 *  @return {@code true} if the role has write access. {@code false}
	 *  otherwise.
	 */
	public boolean getRoleWriteAccess(String roleName) {
	}

	/**
	 *  Set whether users belonging to the role with the given roleName are
	 *  allowed to write this object.
	 * 
	 *  @param roleName The name of the role.
	 *  @param allowed Whether the given role can write this object.
	 */
	public void setRoleWriteAccess(String roleName, boolean allowed) {
	}

	/**
	 *  Get whether users belonging to the given role are allowed to read this
	 *  object. Even if this returns {@code false}, the role may still be able to
	 *  read it if a parent role has read access. The role must already be saved
	 *  on the server and its data must have been fetched in order to use this
	 *  method.
	 * 
	 *  @param role The role to check for access.
	 *  @return {@code true} if the role has read access. {@code false}
	 *  otherwise.
	 */
	public boolean getRoleReadAccess(ParseRole role) {
	}

	/**
	 *  Set whether users belonging to the given role are allowed to read this
	 *  object. The role must already be saved on the server and its data must
	 *  have been fetched in order to use this method.
	 * 
	 *  @param role The role to assign access.
	 *  @param allowed Whether the given role can read this object.
	 */
	public void setRoleReadAccess(ParseRole role, boolean allowed) {
	}

	/**
	 *  Get whether users belonging to the given role are allowed to write this
	 *  object. Even if this returns {@code false}, the role may still be able to
	 *  write it if a parent role has write access. The role must already be
	 *  saved on the server and its data must have been fetched in order to use
	 *  this method.
	 * 
	 *  @param role The role to check for access.
	 *  @return {@code true} if the role has write access. {@code false}
	 *  otherwise.
	 */
	public boolean getRoleWriteAccess(ParseRole role) {
	}

	/**
	 *  Set whether users belonging to the given role are allowed to write this
	 *  object. The role must already be saved on the server and its data must
	 *  have been fetched in order to use this method.
	 * 
	 *  @param role The role to assign access.
	 *  @param allowed Whether the given role can write this object.
	 */
	public void setRoleWriteAccess(ParseRole role, boolean allowed) {
	}
}
