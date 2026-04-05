# 📖 Pagination Learning Guide

## What is Pagination?

**Pagination** is dividing large datasets into smaller chunks (pages) so users can view them in manageable pieces.

### Real-World Examples

```
E-commerce: Show 10 products per page (page 1, page 2, page 3...)
Social media: Show 20 posts per page (Twitter, Facebook)
Search: Google shows 10 results per page
Database query: Avoid loading millions of records at once
```

---

## Core Concepts

### 1. **Page Number** (0-indexed)
```
Page 0 = First page (products 1-10)
Page 1 = Second page (products 11-20)
Page 2 = Third page (products 21-30)
...
```

### 2. **Page Size** (items per page)
```
Size 5 = 5 items per page
Size 10 = 10 items per page
Size 50 = 50 items per page
```

### 3. **Total Elements**
```
50 total products
Size 10 per page
= 5 total pages
```

### 4. **Sorting**
```
Sort by name ASC (A to Z)
Sort by price DESC (High to Low)
Sort by date (Newest first)
```

---

## Your Project Structure

```
pagination-project/
├── pom.xml
├── src/main/
│   ├── java/com/example/pagination/
│   │   ├── PaginationLearningApplication.java
│   │   ├── model/
│   │   │   └── Product.java
│   │   ├── repository/
│   │   │   └── ProductRepository.java
│   │   └── controller/
│   │       └── ProductController.java
│   └── resources/
│       ├── application.yml
│       └── data.sql  (50 sample products)
```

---

## Setup & Run

### Step 1: Navigate to project
```bash
cd pagination-project
```

### Step 2: Build
```bash
mvn clean install
```

### Step 3: Run
```bash
mvn spring-boot:run
```

### Step 4: See it working
```
Started PaginationLearningApplication in X seconds
```

---

## Test All 8 Pagination Examples

### EXAMPLE 1: Basic Pagination
```bash
curl "http://localhost:8080/api/products/basic?page=0&size=5"
```

**What happens:**
- Page 0 = First 5 products
- Response includes total pages, hasNext, hasPrevious

**Try this:**
```bash
# Get page 0 (first 5)
curl "http://localhost:8080/api/products/basic?page=0&size=5"

# Get page 1 (next 5)
curl "http://localhost:8080/api/products/basic?page=1&size=5"

# Get page 2 (next 5)
curl "http://localhost:8080/api/products/basic?page=2&size=5"

# Change size
curl "http://localhost:8080/api/products/basic?page=0&size=10"
```

---

### EXAMPLE 2: Pagination with Sorting
```bash
# Sort by price ascending
curl "http://localhost:8080/api/products/sorted?page=0&size=5&sortBy=price&sortDir=ASC"

# Sort by price descending
curl "http://localhost:8080/api/products/sorted?page=0&size=5&sortBy=price&sortDir=DESC"

# Sort by name
curl "http://localhost:8080/api/products/sorted?page=0&size=5&sortBy=name&sortDir=ASC"
```

**What it does:**
- Shows cheapest products first (ASC) or most expensive (DESC)

---

### EXAMPLE 3: Multiple Sorts
```bash
curl "http://localhost:8080/api/products/multi-sort?page=0&size=5"
```

**What it does:**
- Sorts by price DESC, then by name ASC
- Products ordered by price first, then by name

---

### EXAMPLE 4: Search with Pagination
```bash
# Search for "phone"
curl "http://localhost:8080/api/products/search?name=phone&page=0&size=5"

# Search for "laptop"
curl "http://localhost:8080/api/products/search?name=laptop&page=0&size=5"

# Search for "headphones"
curl "http://localhost:8080/api/products/search?name=headphones&page=0&size=5"
```

**What it does:**
- Finds products containing the search term
- Paginates the search results

---

### EXAMPLE 5: Price Range Filter
```bash
# Products between $100 and $500
curl "http://localhost:8080/api/products/price-range?minPrice=100&maxPrice=500&page=0&size=5"

# Products between $500 and $2000
curl "http://localhost:8080/api/products/price-range?minPrice=500&maxPrice=2000&page=0&size=5"
```

**What it does:**
- Filters by price range
- Paginates filtered results

---

### EXAMPLE 6: Custom Query
```bash
# Products more expensive than $500
curl "http://localhost:8080/api/products/expensive?minPrice=500&page=0&size=5"

# Products more expensive than $1000
curl "http://localhost:8080/api/products/expensive?minPrice=1000&page=0&size=5"
```

---

### EXAMPLE 7: Complete Pagination Response
```bash
curl "http://localhost:8080/api/products/complete?page=0&size=10&sortBy=price&sortDir=DESC&search=phone"
```

**What it returns:**
- Pagination info (current page, total pages, hasNext, etc.)
- Navigation URLs (nextPage, previousPage, etc.)
- The actual data
- Search information

---

### EXAMPLE 8: Manual Pagination
```bash
# Offset 0, limit 5 (same as page 0, size 5)
curl "http://localhost:8080/api/products/manual?offset=0&limit=5"

# Offset 5, limit 5 (same as page 1, size 5)
curl "http://localhost:8080/api/products/manual?offset=5&limit=5"

# Offset 10, limit 5 (same as page 2, size 5)
curl "http://localhost:8080/api/products/manual?offset=10&limit=5"
```

**What it does:**
- Shows how pagination works under the hood
- offset = skip N records, limit = take N records

---

## Understanding the Response

```json
{
  "content": [
    {
      "id": 1,
      "name": "iPhone 15",
      "price": 999.99,
      "description": "Latest Apple smartphone",
      "stock": 50,
      "createdAt": 1700000000000
    },
    ...
  ],
  "totalElements": 50,
  "totalPages": 10,
  "currentPage": 0,
  "pageSize": 5,
  "hasNext": true,
  "hasPrevious": false
}
```

**Field explanation:**
- `content`: The actual products for this page
- `totalElements`: Total number of products (50)
- `totalPages`: How many pages total (50 / 5 = 10 pages)
- `currentPage`: Which page you're on (0 = first)
- `pageSize`: Items per page
- `hasNext`: Are there more pages?
- `hasPrevious`: Can you go to previous page?

---

## Key Concepts to Understand

### Pageable Interface
```java
Pageable pageable = PageRequest.of(page, size, sort);
```

This creates an object that tells Spring Data:
- Which page (0-indexed)
- How many items per page
- How to sort

### Page Object
```java
Page<Product> products = productRepository.findAll(pageable);
```

This returns:
- The actual data (products)
- Pagination metadata (total pages, hasNext, etc.)

### Repository Methods
```java
// Simple pagination
Page<Product> findAll(Pageable pageable);

// With filter
Page<Product> findByNameContaining(String name, Pageable pageable);

// Custom query
@Query("SELECT p FROM Product p WHERE p.price > :minPrice")
Page<Product> findExpensiveProducts(@Param("minPrice") Double minPrice, Pageable pageable);
```

---

## Frontend Logic (Pseudocode)

```javascript
// Page 0, size 10
const response = fetch('/api/products/basic?page=0&size=10')
const data = response.json()

// Display products
data.content.forEach(product => {
  console.log(product.name, product.price)
})

// Show page info
console.log(`Page ${data.currentPage + 1} of ${data.totalPages}`)

// Show next/previous buttons
if (data.hasNext) {
  showButton("Next", () => loadPage(data.currentPage + 1))
}
if (data.hasPrevious) {
  showButton("Previous", () => loadPage(data.currentPage - 1))
}
```

---

## Common Pagination Patterns

### Pattern 1: Numbered Pages
```
[1] [2] [3] [4] [5] → ... [50]
Click a page number to jump to that page
```

### Pattern 2: Prev/Next
```
[Previous] Page 1 of 10 [Next]
Simple navigation, one page at a time
```

### Pattern 3: Infinite Scroll
```
User scrolls down → load more products → append to list
Like Instagram or Twitter
```

### Pattern 4: Offset/Limit
```
Load 5 more results
offset=0, limit=5
offset=5, limit=5
offset=10, limit=5
```

---

## Performance Tips

### 1. Don't fetch all data
```java
// ❌ BAD: Loads all 1 million products
List<Product> all = productRepository.findAll();

// ✅ GOOD: Loads only page of 10
Page<Product> page = productRepository.findAll(PageRequest.of(0, 10));
```

### 2. Add indexes for sorting
```java
@Column(nullable = false, name = "created_at")
@Index(name = "idx_created_at")
private Long createdAt;
```

### 3. Limit page size
```java
// Don't allow huge pages
if (size > 100) {
  size = 100;  // Max 100 items per page
}
```

---

## Testing Checklist

- [ ] Get page 0 with basic pagination
- [ ] Go to page 1, page 2
- [ ] Sort by price ascending
- [ ] Sort by price descending
- [ ] Sort by name
- [ ] Search for "phone"
- [ ] Search for "laptop"
- [ ] Filter by price range ($100-$500)
- [ ] Get expensive products ($1000+)
- [ ] Test with different page sizes (5, 10, 20, 50)
- [ ] Verify hasNext and hasPrevious flags
- [ ] Check totalPages calculation

---

## What You've Learned

✅ How pagination divides data into pages  
✅ How page numbers and sizes work  
✅ How to sort paginated data  
✅ How to filter and search with pagination  
✅ How to implement pagination in Spring Data  
✅ How to handle pagination responses  
✅ Different pagination patterns  
✅ Performance considerations

---

## Next Steps

1. Test all 8 examples with curl
2. Modify page sizes and see results
3. Try different searches
4. Combine filters with sorting
5. Look at the ProductController code
6. Understand PageRequest and Page objects
7. Add your own endpoints!

---

## Pro Tips

### Add limit to prevent abuse
```java
if (size > 100) size = 100;
```

### Default to reasonable values
```java
@RequestParam(defaultValue = "0") int page,
@RequestParam(defaultValue = "10") int size
```

### Use URL encoding for searches
```bash
curl "http://localhost:8080/api/products/search?name=phone&page=0&size=5"
# Space becomes %20 in URL
curl "http://localhost:8080/api/products/search?name=gaming%20laptop&page=0&size=5"
```

### Calculate if more results exist
```java
boolean hasMore = (offset + limit) < totalCount;
```

---

Enjoy learning pagination! 🚀