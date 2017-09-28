/*******************************************************************************
 * Copyright (c) Intel Corporation
 * Copyright (c) 2017
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.osc.core.broker.service.servicefunctionchain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.osc.core.broker.service.exceptions.VmidcBrokerInvalidEntryException;
import org.osc.core.broker.service.exceptions.VmidcBrokerValidationException;
import org.osc.core.broker.service.request.BaseIdRequest;
import org.osc.core.broker.service.response.EmptySuccessResponse;

@RunWith(MockitoJUnitRunner.class)
public class DeleteServiceFunctionChainServiceTest extends BaseServiceFunctionChainServiceTest {

	@InjectMocks
	private DeleteServiceFunctionChainService service;
	
	private BaseIdRequest request;
	
	@Override
	@Before
	public void testInitialize() throws Exception {
		super.testInitialize();
		this.service.validator = this.validatorMock;
		request = new BaseIdRequest();
		Mockito.when(this.service.validator.create(this.em)).thenReturn(this.validatorMock);
	}

	@Test
	public void testDispatch_WithNullRequest_ThrowsNullPointerException() throws Exception {
		// Arrange.
		this.exception.expect(NullPointerException.class);

		// Act.
		this.service.dispatch(null);
	}

	@Test
	public void testDispatch_WithNullSfcId_ThrowsVmidcBrokerInvalidEntryException() throws Exception {
		Mockito.when(this.service.validator.create(this.em).validateVirtualConnector(em, this.vc.getId())).thenCallRealMethod();
		request.setParentId(this.vc.getId());
		this.exception.expect(VmidcBrokerInvalidEntryException.class);
		this.exception.expectMessage("Id should not have an empty value.");

		// Act.
		this.service.dispatch(request);
	}

	@Test
	public void testDispatch_WithInvalidSfcId_VmidcBrokerValidationException() throws Exception {
		
		Mockito.when(this.service.validator.create(this.em).validateVirtualConnector(em, this.vc.getId())).thenCallRealMethod();
		request.setId(222L);
		request.setParentId(this.vc.getId());

		this.exception.expect(VmidcBrokerValidationException.class);
		this.exception.expectMessage("Service Function Chain with Id " + request.getId() + " is not found.");

		// Act.
		this.service.dispatch(request);
	}
	
	@Test
	public void testDispatch_WithInvalidVcId_VmidcBrokerValidationException() throws Exception {	
		Mockito.when(this.service.validator.create(this.em).validateVirtualConnector(em, 32L)).thenCallRealMethod();
		request.setId(this.sfc.getId());
		request.setParentId(32L);

		this.exception.expect(VmidcBrokerValidationException.class);
		this.exception.expectMessage("Virtualization Connector id " + request.getParentId() + " is not found.");

		// Act.
		this.service.dispatch(request);
	}


	@Test
	public void testDispatch_WhenRequestIsValid_ValidationSucceeds() throws Exception {
		Mockito.when(this.service.validator.create(this.em).validateVirtualConnector(em, this.vc.getId())).thenCallRealMethod();
		request.setId(this.sfc.getId());
		request.setParentId(this.vc.getId());
		// Act.
		EmptySuccessResponse response = this.service.dispatch(request);
		Assert.assertNotNull("The returned response should not be null.", response);

	}

}