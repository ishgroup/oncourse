package ish.oncourse.model;

import org.apache.cayenne.ObjectId;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WebContentComparatorTest {
	
	private class WebContentVisibilityFacade {
		private WebContent webcontent;
		
		private WebContentVisibilityFacade(WebContent webcontent) {
			this.webcontent = webcontent;
		}
		
		private WebContentVisibility getWebContentVisibility() {
			return new WebContentVisibility() {
				private static final long serialVersionUID = 1L;
				@Override
				public Integer getWeight() {
					return 1;
				}

				@Override
				public WebContent getWebContent() {
					return webcontent;
				};
			};
		}
	}
	
	@Test
	public void testComparatorForPersistedObjects() {
		WebContent wc1 = new WebContent() {
			private static final long serialVersionUID = 1L;

			@Override
			public Long getId() {return 1l;}
			
			@Override
			public String getContent() {return "wc1_content";}
			
			@Override
			public String getName() {return "wc1_name";}

			@Override
			public WebContentVisibility getWebContentVisibility(WebNodeType webNodeType) {
				return new WebContentVisibilityFacade(this).getWebContentVisibility();
			}
		}, 
		wc2 = new WebContent() {
			private static final long serialVersionUID = 1L;

			@Override
			public Long getId() {return 2l;}
			
			@Override
			public String getContent() {return "wc2_content";}
			
			@Override
			public String getName() {return "wc2_name";}
			
			@Override
			public WebContentVisibility getWebContentVisibility(WebNodeType webNodeType) {return null;}
		},
		wc3 = new WebContent() {
			private static final long serialVersionUID = 1L;

			@Override
			public Long getId() {return 2l;}
			
			@Override
			public String getContent() {return "wc2_content";}
			
			@Override
			public String getName() {return "wc2_name";}
			
			@Override
			public WebContentVisibility getWebContentVisibility(WebNodeType webNodeType) {
				return new WebContentVisibility() {
					private static final long serialVersionUID = 1L;
					@Override
					public Integer getWeight() {
						return 0;
					}
				};
			}
		};
		
		WebNodeType wnt = mock(WebNodeType.class);
		ObjectId objectId = mock(ObjectId.class);
		when(wnt.getObjectId()).thenReturn(objectId);
		when(objectId.isTemporary()).thenReturn(false);
		
		assertEquals("Comparator for persisted object should return the same result as basic objects compare if one of the web content have no visibility", 
				new WebContentComparator(wnt).compare(wc1, wc2), wc1.compareTo(wc2));
		//System.out.println(wc1.compareTo(wc2));
		
		assertEquals("Comparator for persisted object should return the same result as basic objects compare if both of the web content have visibility", 
				new WebContentComparator(wnt).compare(wc1, wc3), wc1.getWebContentVisibility(wnt).compareTo(wc3.getWebContentVisibility(wnt)));
		//System.out.println(wc1.getWebContentVisibility(wnt).compareTo(wc3.getWebContentVisibility(wnt)));
	}
	
	@Test
	public void testComparatorForTemporaryObjects() {
		WebContent wc1 = new WebContent() {
			private static final long serialVersionUID = 1L;

			@Override
			public Long getId() {return 1l;}
			
			@Override
			public String getContent() {return "wc1_content";}
			
			@Override
			public String getName() {return "wc1_name";}
		}, 
		wc2 = new WebContent() {
			private static final long serialVersionUID = 1L;

			@Override
			public Long getId() {return 2l;}
			
			@Override
			public String getContent() {return "wc2_content";}
			
			@Override
			public String getName() {return "wc2_name";}
		};
		
		WebNodeType wnt = mock(WebNodeType.class);
		ObjectId objectId = mock(ObjectId.class);
		when(wnt.getObjectId()).thenReturn(objectId);
		when(objectId.isTemporary()).thenReturn(true);
		
		assertEquals("Comparator for temporary object should return the same result as basic objects compare", 
			new WebContentComparator(wnt).compare(wc1, wc2), wc1.compareTo(wc2));
		//System.out.println(wc1.compareTo(wc2));
	}
}
