from bson import ObjectId
from fastapi import FastAPI
from pydantic import BaseModel
from typing import List, Optional
from pymongo import MongoClient
from sentence_transformers import SentenceTransformer, util
import torch

app = FastAPI()

# Kết nối MongoDB
client = MongoClient(
    "mongodb+srv://sportuser:0822492004Rain@rain.v66ge.mongodb.net/sportdb?retryWrites=true&w=majority&appName=rain"
)
db = client["sportdb"]
product_collection = db["Product"]
category_collection = db["Category"]

# Load mô hình SBERT
model = SentenceTransformer('all-MiniLM-L6-v2')

# Models request/response
class SearchHistoryItem(BaseModel):
    keyword: str

class OrderProductItem(BaseModel):
    product_id: str
    category: Optional[str] = None
    description: Optional[str] = None
    title: Optional[str] = None
    brand: Optional[str] = None 

class RecommendRequest(BaseModel):
    user_id: str
    search_history: List[SearchHistoryItem]
    purchased_products: List[OrderProductItem]

class RecommendResponse(BaseModel):
    recommended_product_ids: List[str]

@app.post("/recommend", response_model=RecommendResponse)
def recommend(data: RecommendRequest):
    products = list(product_collection.find({}, {
        "_id": 1,
        "productCategory": 1,
        "productDescription": 1,
        "productTitle": 1,
        "productBrand": 1
    }))

    # Lấy thêm categoryType (tên category) từ collection Category
    for p in products:
        cat_id = p.get("productCategory")
        category_name = ""
        if cat_id:
            cat_doc = category_collection.find_one({"_id": ObjectId(cat_id)})
            if cat_doc:
                category_name = cat_doc.get("categoryType", "")
        p["categoryType"] = category_name

        p["id"] = str(p["_id"])
        del p["_id"]

    product_ids = []
    product_texts = []

    # Ghép chuỗi title, brand, category name, description
    for p in products:
        title = p.get("productTitle", "")
        brand = p.get("productBrand", "")
        category = p.get("categoryType", "")
        description = p.get("productDescription", "")

        text = f"{title} {brand} {category} {description}".strip()
        if text:
            product_ids.append(p["id"])
            product_texts.append(text)

    if not product_texts:
        return RecommendResponse(recommended_product_ids=[])

    # Tạo embedding toàn bộ sản phẩm
    corpus_embeddings = model.encode(product_texts, convert_to_tensor=True)

    # Embedding lịch sử tìm kiếm
    search_texts = [item.keyword for item in data.search_history] if data.search_history else []
    # Embedding sản phẩm đã mua
    purchased_texts = []
    for p in data.purchased_products:
        title = p.title or ""
        brand = p.brand or ""
        cat = p.category or ""
        desc = p.description or ""
        text = f"{title} {brand} {cat} {desc}".strip()
        purchased_texts.append(text)

    user_texts = search_texts + purchased_texts
    if not user_texts:
        return RecommendResponse(recommended_product_ids=[])

    user_embedding = model.encode(user_texts, convert_to_tensor=True)
    user_embedding = torch.mean(user_embedding, dim=0, keepdim=True) 
    # Tính cosine similarity
    cosine_scores = util.cos_sim(user_embedding, corpus_embeddings)[0]

    # Lấy top 5 sản phẩm tương tự nhất
    top_results = torch.topk(cosine_scores, k=8)

    recommended = []
    for idx in top_results.indices.cpu().numpy():
        recommended.append(product_ids[idx])

    return RecommendResponse(recommended_product_ids=recommended)
