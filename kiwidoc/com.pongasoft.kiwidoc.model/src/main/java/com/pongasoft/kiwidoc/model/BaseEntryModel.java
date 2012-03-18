
/*
 * Copyright (c) 2012 Yan Pujante
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.pongasoft.kiwidoc.model;

/**
 * @author yan@pongasoft.com
 */
public class BaseEntryModel
{
  public static final int PUBLIC_API_ACCESS = Access.or(Access.PUBLIC, Access.PROTECTED);

  public static enum Access
  {
    ABSTRACT(1024),
    ANNOTATION(8192),
    BRIDGE(64),
    DEPRECATED(131072),
    ENUM(16384),
    FINAL(16),
    INTERFACE(512),
    NATIVE(256),
    PRIVATE(2),
    PROTECTED(4),
    PUBLIC(1),
    STATIC(8),
    STRICT(2048),
    SUPER(32),
    SYNCHRONIZED(32),
    SYNTHETIC(4096),
    TRANSIENT(128),
    VARARGS(128),
    VOLATILE(64),
    // this op code is user for exported vs non exported and is not part
    // of the 'normal' op codes... this make it easy to check as a negative access code
    // means non exported
    NON_EXPORTED(0x80000000);

    private final int _opcode;

    private Access(int opcode)
    {
      _opcode = opcode;
    }

    public int getOpcode()
    {
      return _opcode;
    }

    public boolean matches(int access)
    {
      return (access & _opcode) != 0;
    }

    /**
     * @return the op code represented by the or of all the accesses
     */
    public static int or(Access... accesses)
    {
      int res = 0;
      for(Access access : accesses)
      {
        res |= access.getOpcode();
      }
      return res;
    }
  }

  private final int _access;
  private final String _name;

  /**
   * Constructor
   */
  public BaseEntryModel(int access, String name)
  {
    _access = access;
    _name = name;
  }

  public int getAccess()
  {
    return _access;
  }

  public boolean is(Access access)
  {
    return access.matches(getAccess());
  }

  public boolean isAny(int access)
  {
    return (_access & access) != 0;
  }

  public boolean isExactly(int access)
  {
    return _access == access;
  }

  public String getName()
  {
    return _name;
  }

  public boolean isExported()
  {
    return _access >= 0;
  }

  /**
   * @return <code>true</code> if this entry is part of the public API, in other words
   * it is not an implementation detail and it should be displayed part of a 'normal' api.
   */
  public boolean isPublicAPI()
  {
    return isExported() && isAny(PUBLIC_API_ACCESS);
  }
}