/**
 * Copyright 2009-2015 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ibatis.type;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EnumOrdinalTypeHandlerTest extends BaseTypeHandlerTest {

    private static final TypeHandler<MyEnum> TYPE_HANDLER = new EnumOrdinalTypeHandler<MyEnum>(MyEnum.class);

    @Test
    public void shouldSetParameter() throws Exception {
        TYPE_HANDLER.setParameter(ps, 1, MyEnum.ONE, null);
        verify(ps).setInt(1, 0);
    }

    @Test
    public void shouldSetNullParameter() throws Exception {
        TYPE_HANDLER.setParameter(ps, 1, null, JdbcType.VARCHAR);
        verify(ps).setNull(1, JdbcType.VARCHAR.TYPE_CODE);
    }

    @Test
    public void shouldGetResultFromResultSet() throws Exception {
        when(rs.getInt("column")).thenReturn(0);
        when(rs.wasNull()).thenReturn(false);
        assertEquals(MyEnum.ONE, TYPE_HANDLER.getResult(rs, "column"));
    }

    @Test
    public void shouldGetNullResultFromResultSet() throws Exception {
        when(rs.getInt("column")).thenReturn(0);
        when(rs.wasNull()).thenReturn(true);
        assertEquals(null, TYPE_HANDLER.getResult(rs, "column"));
    }

    @Test
    public void shouldGetResultFromCallableStatement() throws Exception {
        when(cs.getInt(1)).thenReturn(0);
        when(cs.wasNull()).thenReturn(false);
        assertEquals(MyEnum.ONE, TYPE_HANDLER.getResult(cs, 1));
    }

    @Test
    public void shouldGetNullResultFromCallableStatement() throws Exception {
        when(cs.getInt(1)).thenReturn(0);
        when(cs.wasNull()).thenReturn(true);
        assertEquals(null, TYPE_HANDLER.getResult(cs, 1));
    }

    enum MyEnum {
        ONE, TWO
    }

}
