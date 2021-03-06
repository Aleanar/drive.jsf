package fr.isima.drivejsf.ejb;

import fr.isima.drivejsf.dao.DocumentDAO;
import fr.isima.drivejsf.entity.Data;
import fr.isima.drivejsf.entity.Document;
import fr.isima.drivejsf.entity.Share;
import fr.isima.drivejsf.exception.NoDataFoundException;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;

import java.util.ArrayList;
import java.util.List;

@Singleton
@LocalBean
public class DocumentServiceEJB {

    @EJB
    private UserServiceEJB userService;

    @EJB
    private DataServiceEJB dataService;

    @EJB
    private ShareServiceEJB shareService;

    public DocumentServiceEJB() {
    	
    }

    public boolean isRoot (String documentId) {
        return new DocumentDAO().isRoot(Integer.parseInt(documentId));
    }

    public boolean isFolder (String documentId) {
        return new DocumentDAO().isFolder(Integer.parseInt(documentId));
    }

    public void deleteDocument (String documentId) {
        new DocumentDAO().deleteDocument(Integer.parseInt(documentId));
    }

    public List<Document> getList (String ownerId, String documentId) throws NoDataFoundException {

        if (documentId == null)
            return new DocumentDAO().getDocumentRoot(Integer.parseInt(ownerId));

        return new DocumentDAO().getFolderList(Integer.parseInt(documentId));

    }

    public List<Document> getSharedWithMeList (String ownerId) {

        List<Share> sharedElements = shareService.getSharedWithMe(Integer.parseInt(ownerId));
        List<Document> documents = new ArrayList<Document>();

        for (Share share : sharedElements)
        {
            documents.add(share.getDocid());
        }

        return documents;

    }

    public List<Share> getSharedList (String ownerId) {

        List<Share> sharedElements = shareService.getShared(Integer.parseInt(ownerId));
        List<Document> documents = new ArrayList<Document>();

        /*for (Share share : sharedElements)
        {
            documents.add(share.getDocid());
        }*/

        return sharedElements;

    }

    public Document getDocument (int documentId) {
        return new DocumentDAO().getDocument(documentId);
    }

    public Document getDocumentForUri (String ownerId, String documentUri) {
        return new DocumentDAO().getDocumentForUri(Integer.parseInt(ownerId), documentUri);
    }

    public void addDocument(UploadedFile file, Document current, String userId) {
        Document document = new Document();

        document.setParentid(current);
        document.setName(file.getFileName());
        document.setOwnerid(userService.getUser(userId));

        if (current == null)
            document.setUri(file.getFileName());
        else
            document.setUri(current.getUri() + "/" + file.getFileName());

        // Manage data
        Data data = dataService.getDataForByteArray(file.getContents());
        document.setDataid(data);

        new DocumentDAO().saveDocument(document);
    }

    public void addFolder(String folderName, Document current, String userId) {
        Document document = new Document();

        document.setParentid(current);
        document.setName(folderName);
        document.setOwnerid(userService.getUser(userId));

        if (current == null)
            document.setUri(folderName);
        else
            document.setUri(current.getUri() + "/" + folderName);

        // Manage data
        document.setDataid(null);

        new DocumentDAO().saveDocument(document);
    }

    public List<Document> searchDocuments(String searchInput, String currentUserId) {
        return new DocumentDAO().searchDocuments(searchInput, Integer.parseInt(currentUserId));
    }
}
